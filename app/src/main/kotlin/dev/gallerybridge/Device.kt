package dev.gallerybridge

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.TrafficStats
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock

internal data class DeviceSnapshot(
    val wifi: Boolean, val charging: Boolean, val battery: Int?, val thermal: Int,
    val headroom: Float?, val helperRx: Long?, val helperTx: Long?, val free: Long
)

internal class Device(private val context: Context) {
    private val network = context.getSystemService(ConnectivityManager::class.java)
    private val power = context.getSystemService(PowerManager::class.java)
    private val battery = context.getSystemService(BatteryManager::class.java)
    private var sampled = -30_000L
    private var headroom: Float? = null

    @Synchronized
    fun state(): DeviceSnapshot {
        val connection = network.getNetworkCapabilities(network.activeNetwork)
        val wifi = connection?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true &&
            connection.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        val now = SystemClock.elapsedRealtime()
        if (Build.VERSION.SDK_INT >= 30 && now - sampled >= 10_000) {
            headroom = power.getThermalHeadroom(0).takeIf(Float::isFinite)
            sampled = now
        }
        val uid = android.os.Process.myUid()
        return DeviceSnapshot(wifi, battery.isCharging, battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            .takeIf { it in 0..100 }, power.currentThermalStatus, headroom,
            TrafficStats.getUidRxBytes(uid).takeIf { it >= 0 }, TrafficStats.getUidTxBytes(uid).takeIf { it >= 0 },
            context.filesDir.usableSpace)
    }
}

internal class WorkGate(
    private val device: Device, private val battery: Int, private val onlyCharging: Boolean,
    private val paused: () -> Boolean, private val reserve: Long = 0
) {
    private var last = -10_000L

    fun check() {
        demand(!paused(), Reason.PAUSED)
        val now = SystemClock.elapsedRealtime()
        if (now - last < 500) return
        val state = device.state()
        blockReason(Conditions(state.wifi, battery, state.battery, state.charging, onlyCharging,
            state.thermal, state.headroom, false))?.let { throw BridgeFailure(it) }
        demand(state.free >= reserve, Reason.SPACE)
        last = now
        if (state.thermal == PowerManager.THERMAL_STATUS_MODERATE) Thread.sleep(45)
    }
}

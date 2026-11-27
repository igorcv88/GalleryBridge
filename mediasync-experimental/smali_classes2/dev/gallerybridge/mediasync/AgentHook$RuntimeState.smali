.class final Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;
.super Ljava/lang/Object;
.source "AgentHook.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Ldev/gallerybridge/mediasync/AgentHook;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1a
    name = "RuntimeState"
.end annotation

.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;
    }
.end annotation


# static fields
.field private static final OFF:Ldev/gallerybridge/mediasync/Policy$Settings;


# instance fields
.field private final context:Landroid/content/Context;

.field private final lastEvent:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map<",
            "Ljava/lang/String;",
            "Ljava/lang/Long;",
            ">;"
        }
    .end annotation
.end field

.field private volatile latched:Z

.field private final listeners:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map<",
            "Ljava/lang/Object;",
            "Ljava/lang/Boolean;",
            ">;"
        }
    .end annotation
.end field

.field private final power:Landroid/os/PowerManager;

.field private volatile prediction:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;

.field private final preferences:Lde/robv/android/xposed/XSharedPreferences;

.field private final sdkListener:Ljava/lang/reflect/Method;

.field private volatile settings:Ldev/gallerybridge/mediasync/Policy$Settings;


# direct methods
.method public static synthetic $r8$lambda$JYpxyvzsoSYbY68DZf8x8IETRl8(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;I)V
    .locals 0

    invoke-direct {p0, p1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->lambda$start$0(I)V

    return-void
.end method

.method public static synthetic $r8$lambda$mCEi04OcFi8gu0FD463twzV2nG8(Landroid/os/Handler;Ljava/lang/Runnable;)Z
    .locals 0

    invoke-virtual {p0, p1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    move-result p0

    return p0
.end method

.method static bridge synthetic -$$Nest$fgetlatched(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Z
    .locals 0

    iget-boolean p0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->latched:Z

    return p0
.end method

.method static bridge synthetic -$$Nest$fgetpower(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Landroid/os/PowerManager;
    .locals 0

    iget-object p0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->power:Landroid/os/PowerManager;

    return-object p0
.end method

.method static bridge synthetic -$$Nest$fgetpreferences(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Lde/robv/android/xposed/XSharedPreferences;
    .locals 0

    iget-object p0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->preferences:Lde/robv/android/xposed/XSharedPreferences;

    return-object p0
.end method

.method static bridge synthetic -$$Nest$fgetsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Ldev/gallerybridge/mediasync/Policy$Settings;
    .locals 0

    iget-object p0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->settings:Ldev/gallerybridge/mediasync/Policy$Settings;

    return-object p0
.end method

.method static bridge synthetic -$$Nest$fputlatched(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Z)V
    .locals 0

    iput-boolean p1, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->latched:Z

    return-void
.end method

.method static bridge synthetic -$$Nest$fputprediction(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;)V
    .locals 0

    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->prediction:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;

    return-void
.end method

.method static bridge synthetic -$$Nest$fputsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ldev/gallerybridge/mediasync/Policy$Settings;)V
    .locals 0

    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->settings:Ldev/gallerybridge/mediasync/Policy$Settings;

    return-void
.end method

.method static bridge synthetic -$$Nest$mrevokeIfNeeded(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V
    .locals 0

    invoke-direct {p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->revokeIfNeeded()V

    return-void
.end method

.method static bridge synthetic -$$Nest$sfgetOFF()Ldev/gallerybridge/mediasync/Policy$Settings;
    .locals 1

    sget-object v0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->OFF:Ldev/gallerybridge/mediasync/Policy$Settings;

    return-object v0
.end method

.method static constructor <clinit>()V
    .locals 7

    .line 353
    new-instance v0, Ldev/gallerybridge/mediasync/Policy$Settings;

    sget-object v1, Ldev/gallerybridge/mediasync/Policy$Profile;->OFF:Ldev/gallerybridge/mediasync/Policy$Profile;

    const/16 v5, 0xf

    const/4 v6, 0x0

    const/4 v2, 0x0

    const/4 v3, 0x0

    const/16 v4, 0x3c

    invoke-direct/range {v0 .. v6}, Ldev/gallerybridge/mediasync/Policy$Settings;-><init>(Ldev/gallerybridge/mediasync/Policy$Profile;ZZIIZ)V

    sput-object v0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->OFF:Ldev/gallerybridge/mediasync/Policy$Settings;

    return-void
.end method

.method constructor <init>(Landroid/content/Context;Ljava/lang/reflect/Method;)V
    .locals 5
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "sdkListener"    # Ljava/lang/reflect/Method;

    .line 365
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 357
    new-instance v0, Ljava/util/WeakHashMap;

    invoke-direct {v0}, Ljava/util/WeakHashMap;-><init>()V

    invoke-static {v0}, Ljava/util/Collections;->synchronizedMap(Ljava/util/Map;)Ljava/util/Map;

    move-result-object v0

    iput-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->listeners:Ljava/util/Map;

    .line 358
    new-instance v0, Lde/robv/android/xposed/XSharedPreferences;

    const-string v1, "dev.gallerybridge.mediasync"

    const-string v2, "policy"

    invoke-direct {v0, v1, v2}, Lde/robv/android/xposed/XSharedPreferences;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    iput-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->preferences:Lde/robv/android/xposed/XSharedPreferences;

    .line 359
    new-instance v0, Ljava/util/concurrent/ConcurrentHashMap;

    invoke-direct {v0}, Ljava/util/concurrent/ConcurrentHashMap;-><init>()V

    iput-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->lastEvent:Ljava/util/Map;

    .line 360
    sget-object v0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->OFF:Ldev/gallerybridge/mediasync/Policy$Settings;

    iput-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->settings:Ldev/gallerybridge/mediasync/Policy$Settings;

    .line 362
    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;

    const-wide/16 v1, 0x0

    const/4 v3, 0x0

    const/high16 v4, 0x7fc00000    # Float.NaN

    invoke-direct {v0, v4, v1, v2, v3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;-><init>(FJLdev/gallerybridge/mediasync/AgentHook-IA;)V

    iput-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->prediction:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;

    .line 366
    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->context:Landroid/content/Context;

    .line 367
    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->sdkListener:Ljava/lang/reflect/Method;

    .line 368
    const-string v0, "power"

    invoke-virtual {p1, v0}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/os/PowerManager;

    iput-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->power:Landroid/os/PowerManager;

    .line 369
    return-void
.end method

.method static synthetic lambda$revokeIfNeeded$1(Ljava/lang/Throwable;)V
    .locals 0
    .param p0, "error"    # Ljava/lang/Throwable;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 426
    throw p0
.end method

.method private synthetic lambda$start$0(I)V
    .locals 1
    .param p1, "status"    # I

    .line 377
    const/4 v0, 0x3

    if-lt p1, v0, :cond_0

    const/4 v0, 0x1

    iput-boolean v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->latched:Z

    .line 378
    :cond_0
    return-void
.end method

.method private revokeIfNeeded()V
    .locals 8
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 411
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->listeners:Ljava/util/Map;

    invoke-interface {v0}, Ljava/util/Map;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_0

    return-void

    .line 412
    :cond_0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->power:Landroid/os/PowerManager;

    invoke-virtual {v0}, Landroid/os/PowerManager;->getCurrentThermalStatus()I

    move-result v0

    .line 413
    .local v0, "actual":I
    const/4 v1, 0x2

    if-ge v0, v1, :cond_1

    return-void

    .line 414
    :cond_1
    if-ne v0, v1, :cond_2

    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->thermalEnabled()Z

    move-result v1

    if-eqz v1, :cond_2

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->settings:Ldev/gallerybridge/mediasync/Policy$Settings;

    .line 415
    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->sample()Ldev/gallerybridge/mediasync/Policy$Sample;

    move-result-object v2

    iget-boolean v3, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->latched:Z

    invoke-static {v1, v2, v3}, Ldev/gallerybridge/mediasync/Policy;->allowThermal(Ldev/gallerybridge/mediasync/Policy$Settings;Ldev/gallerybridge/mediasync/Policy$Sample;Z)Z

    move-result v1

    if-eqz v1, :cond_2

    return-void

    .line 417
    :cond_2
    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->listeners:Ljava/util/Map;

    monitor-enter v1

    .line 418
    :try_start_0
    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->listeners:Ljava/util/Map;

    invoke-interface {v2}, Ljava/util/Map;->keySet()Ljava/util/Set;

    move-result-object v2

    invoke-interface {v2}, Ljava/util/Set;->toArray()[Ljava/lang/Object;

    move-result-object v2

    .line 419
    .local v2, "pending":[Ljava/lang/Object;
    iget-object v3, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->listeners:Ljava/util/Map;

    invoke-interface {v3}, Ljava/util/Map;->clear()V

    .line 420
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    .line 421
    array-length v1, v2

    const/4 v3, 0x0

    :goto_0
    if-ge v3, v1, :cond_3

    aget-object v4, v2, v3

    .line 423
    .local v4, "listener":Ljava/lang/Object;
    :try_start_1
    iget-object v5, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->sdkListener:Ljava/lang/reflect/Method;

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    filled-new-array {v6}, [Ljava/lang/Object;

    move-result-object v6

    invoke-static {v5, v4, v6}, Lde/robv/android/xposed/XposedBridge;->invokeOriginalMethod(Ljava/lang/reflect/Member;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    .line 424
    const-string v5, "sdk_policy_revoke"

    new-instance v6, Ljava/lang/StringBuilder;

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    const-string v7, "actual_status="

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v6

    const-string v7, "; cancellation=native"

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v6

    invoke-virtual {p0, v5, v6}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->event(Ljava/lang/String;Ljava/lang/String;)V
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 427
    goto :goto_1

    .line 425
    :catchall_0
    move-exception v5

    .line 426
    .local v5, "error":Ljava/lang/Throwable;
    new-instance v6, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$$ExternalSyntheticLambda2;

    invoke-direct {v6, v5}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$$ExternalSyntheticLambda2;-><init>(Ljava/lang/Throwable;)V

    invoke-virtual {p0, v6}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->safely(Ldev/gallerybridge/mediasync/AgentHook$Checked;)V

    .line 421
    .end local v4    # "listener":Ljava/lang/Object;
    .end local v5    # "error":Ljava/lang/Throwable;
    :goto_1
    add-int/lit8 v3, v3, 0x1

    goto :goto_0

    .line 429
    :cond_3
    return-void

    .line 420
    .end local v2    # "pending":[Ljava/lang/Object;
    :catchall_1
    move-exception v2

    :try_start_2
    monitor-exit v1
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_1

    throw v2
.end method


# virtual methods
.method event(Ljava/lang/String;Ljava/lang/String;)V
    .locals 7
    .param p1, "event"    # Ljava/lang/String;
    .param p2, "detail"    # Ljava/lang/String;

    .line 462
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->settings:Ldev/gallerybridge/mediasync/Policy$Settings;

    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/Policy$Settings;->diagnostics()Z

    move-result v0

    if-nez v0, :cond_0

    return-void

    .line 463
    :cond_0
    invoke-static {}, Landroid/os/SystemClock;->elapsedRealtime()J

    move-result-wide v0

    .line 464
    .local v0, "now":J
    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->lastEvent:Ljava/util/Map;

    invoke-interface {v2, p1}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/lang/Long;

    .line 465
    .local v2, "last":Ljava/lang/Long;
    if-eqz v2, :cond_1

    invoke-virtual {v2}, Ljava/lang/Long;->longValue()J

    move-result-wide v3

    sub-long v3, v0, v3

    const-wide/16 v5, 0x2ee

    cmp-long v3, v3, v5

    if-gez v3, :cond_1

    return-void

    .line 466
    :cond_1
    iget-object v3, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->lastEvent:Ljava/util/Map;

    invoke-static {v0, v1}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;

    move-result-object v4

    invoke-interface {v3, p1, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 467
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, "; "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    const-string v4, "GB-MediaSync"

    invoke-static {v4, v3}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 468
    return-void
.end method

.method forget(Ljava/lang/Object;)V
    .locals 1
    .param p1, "listener"    # Ljava/lang/Object;

    .line 408
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->listeners:Ljava/util/Map;

    invoke-interface {v0, p1}, Ljava/util/Map;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    return-void
.end method

.method monitor(Ljava/lang/Object;)V
    .locals 2
    .param p1, "listener"    # Ljava/lang/Object;

    .line 406
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->listeners:Ljava/util/Map;

    sget-object v1, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;

    invoke-interface {v0, p1, v1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    return-void
.end method

.method monitored(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "listener"    # Ljava/lang/Object;

    .line 407
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->listeners:Ljava/util/Map;

    invoke-interface {v0, p1}, Ljava/util/Map;->containsKey(Ljava/lang/Object;)Z

    move-result v0

    return v0
.end method

.method safely(Ldev/gallerybridge/mediasync/AgentHook$Checked;)V
    .locals 3
    .param p1, "action"    # Ldev/gallerybridge/mediasync/AgentHook$Checked;

    .line 454
    :try_start_0
    invoke-interface {p1}, Ldev/gallerybridge/mediasync/AgentHook$Checked;->run()V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 458
    goto :goto_0

    .line 455
    :catchall_0
    move-exception v0

    .line 456
    .local v0, "error":Ljava/lang/Throwable;
    const/4 v1, 0x1

    iput-boolean v1, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->latched:Z

    .line 457
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "intervention=disabled; error="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    const-string v2, "GB-MediaSync"

    invoke-static {v2, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    .line 459
    .end local v0    # "error":Ljava/lang/Throwable;
    :goto_0
    return-void
.end method

.method sample()Ldev/gallerybridge/mediasync/Policy$Sample;
    .locals 15

    .line 433
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->power:Landroid/os/PowerManager;

    if-eqz v0, :cond_6

    .line 434
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->power:Landroid/os/PowerManager;

    invoke-virtual {v0}, Landroid/os/PowerManager;->getCurrentThermalStatus()I

    move-result v2

    .line 435
    .local v2, "thermal":I
    const/4 v0, 0x3

    const/4 v1, 0x1

    if-lt v2, v0, :cond_0

    iput-boolean v1, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->latched:Z

    .line 436
    :cond_0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->context:Landroid/content/Context;

    new-instance v3, Landroid/content/IntentFilter;

    const-string v4, "android.intent.action.BATTERY_CHANGED"

    invoke-direct {v3, v4}, Landroid/content/IntentFilter;-><init>(Ljava/lang/String;)V

    const/4 v4, 0x0

    invoke-virtual {v0, v4, v3}, Landroid/content/Context;->registerReceiver(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;

    move-result-object v0

    .line 437
    .local v0, "intent":Landroid/content/Intent;
    iget-object v10, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->prediction:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;

    .line 438
    .local v10, "predicted":Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;
    const/4 v3, -0x1

    .line 439
    .local v3, "percent":I
    const/4 v4, 0x0

    .local v4, "charging":Z
    const/4 v5, 0x0

    .line 440
    .local v5, "healthy":Z
    if-eqz v0, :cond_5

    .line 441
    const-string v6, "level"

    const/4 v7, -0x1

    invoke-virtual {v0, v6, v7}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I

    move-result v6

    .line 442
    .local v6, "level":I
    const-string v8, "scale"

    invoke-virtual {v0, v8, v7}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I

    move-result v8

    .line 443
    .local v8, "scale":I
    if-ltz v6, :cond_1

    if-gt v6, v8, :cond_1

    if-lez v8, :cond_1

    const-wide/16 v11, 0x64

    int-to-long v13, v6

    mul-long/2addr v13, v11

    int-to-long v11, v8

    div-long/2addr v13, v11

    long-to-int v3, v13

    .line 444
    :cond_1
    const-string v9, "status"

    invoke-virtual {v0, v9, v7}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I

    move-result v9

    .line 445
    .local v9, "status":I
    const-string v11, "plugged"

    const/4 v12, 0x0

    invoke-virtual {v0, v11, v12}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I

    move-result v11

    const/4 v13, 0x2

    if-lez v11, :cond_3

    if-eq v9, v13, :cond_2

    const/4 v11, 0x5

    if-ne v9, v11, :cond_3

    :cond_2
    move v11, v1

    goto :goto_0

    :cond_3
    move v11, v12

    :goto_0
    move v4, v11

    .line 447
    const-string v11, "health"

    invoke-virtual {v0, v11, v7}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I

    move-result v7

    if-ne v7, v13, :cond_4

    goto :goto_1

    :cond_4
    move v1, v12

    :goto_1
    move v5, v1

    move v6, v5

    move v5, v4

    move v4, v3

    goto :goto_2

    .line 440
    .end local v6    # "level":I
    .end local v8    # "scale":I
    .end local v9    # "status":I
    :cond_5
    move v6, v5

    move v5, v4

    move v4, v3

    .line 449
    .end local v3    # "percent":I
    .local v4, "percent":I
    .local v5, "charging":Z
    .local v6, "healthy":Z
    :goto_2
    new-instance v1, Ldev/gallerybridge/mediasync/Policy$Sample;

    invoke-virtual {v10}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->margin()F

    move-result v3

    iget-object v7, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->power:Landroid/os/PowerManager;

    .line 450
    invoke-virtual {v7}, Landroid/os/PowerManager;->isPowerSaveMode()Z

    move-result v7

    invoke-static {}, Landroid/os/SystemClock;->elapsedRealtime()J

    move-result-wide v8

    invoke-virtual {v10}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->timestamp()J

    move-result-wide v11

    sub-long/2addr v8, v11

    invoke-direct/range {v1 .. v9}, Ldev/gallerybridge/mediasync/Policy$Sample;-><init>(IFIZZZJ)V

    .line 449
    return-object v1

    .line 433
    .end local v0    # "intent":Landroid/content/Intent;
    .end local v2    # "thermal":I
    .end local v4    # "percent":I
    .end local v5    # "charging":Z
    .end local v6    # "healthy":Z
    .end local v10    # "predicted":Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;
    :cond_6
    new-instance v0, Ljava/lang/IllegalStateException;

    invoke-direct {v0}, Ljava/lang/IllegalStateException;-><init>()V

    throw v0
.end method

.method start()V
    .locals 5

    .line 372
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->power:Landroid/os/PowerManager;

    if-nez v0, :cond_0

    const/4 v0, 0x1

    iput-boolean v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->latched:Z

    return-void

    .line 373
    :cond_0
    new-instance v0, Landroid/os/HandlerThread;

    const-string v1, "gb-mediasync"

    invoke-direct {v0, v1}, Landroid/os/HandlerThread;-><init>(Ljava/lang/String;)V

    .line 374
    .local v0, "thread":Landroid/os/HandlerThread;
    invoke-virtual {v0}, Landroid/os/HandlerThread;->start()V

    .line 375
    new-instance v1, Landroid/os/Handler;

    invoke-virtual {v0}, Landroid/os/HandlerThread;->getLooper()Landroid/os/Looper;

    move-result-object v2

    invoke-direct {v1, v2}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V

    .line 376
    .local v1, "handler":Landroid/os/Handler;
    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->power:Landroid/os/PowerManager;

    invoke-static {v1}, Ljava/util/Objects;->requireNonNull(Ljava/lang/Object;)Ljava/lang/Object;

    new-instance v3, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$$ExternalSyntheticLambda0;

    invoke-direct {v3, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$$ExternalSyntheticLambda0;-><init>(Landroid/os/Handler;)V

    new-instance v4, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$$ExternalSyntheticLambda1;

    invoke-direct {v4, p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$$ExternalSyntheticLambda1;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V

    invoke-virtual {v2, v3, v4}, Landroid/os/PowerManager;->addThermalStatusListener(Ljava/util/concurrent/Executor;Landroid/os/PowerManager$OnThermalStatusChangedListener;)V

    .line 379
    new-instance v2, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;

    invoke-direct {v2, p0, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Landroid/os/Handler;)V

    invoke-virtual {v1, v2}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 400
    return-void
.end method

.method thermalEnabled()Z
    .locals 2

    .line 403
    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->latched:Z

    if-nez v0, :cond_0

    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->settings:Ldev/gallerybridge/mediasync/Policy$Settings;

    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/Policy$Settings;->profile()Ldev/gallerybridge/mediasync/Policy$Profile;

    move-result-object v0

    sget-object v1, Ldev/gallerybridge/mediasync/Policy$Profile;->CUSTOM:Ldev/gallerybridge/mediasync/Policy$Profile;

    if-ne v0, v1, :cond_0

    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->settings:Ldev/gallerybridge/mediasync/Policy$Settings;

    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/Policy$Settings;->thermal()Z

    move-result v0

    if-eqz v0, :cond_0

    const/4 v0, 0x1

    goto :goto_0

    :cond_0
    const/4 v0, 0x0

    :goto_0
    return v0
.end method

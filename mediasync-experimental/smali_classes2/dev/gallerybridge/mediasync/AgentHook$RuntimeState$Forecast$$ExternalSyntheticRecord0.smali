.class public final synthetic Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast$$ExternalSyntheticRecord0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"


# direct methods
.method public static synthetic m(JF)I
    .locals 0

    .line 0
    invoke-static {p0, p1}, Ljava/lang/Long;->hashCode(J)I

    move-result p0

    mul-int/lit8 p0, p0, 0x1f

    invoke-static {p2}, Ljava/lang/Float;->hashCode(F)I

    move-result p1

    add-int/2addr p0, p1

    return p0
.end method

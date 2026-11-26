.class final Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;
.super Lcom/android/tools/r8/RecordTag;
.source "AgentHook.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1a
    name = "Forecast"
.end annotation


# instance fields
.field private final margin:F

.field private final timestamp:J


# direct methods
.method private synthetic $record$equals(Ljava/lang/Object;)Z
    .locals 4

    instance-of v0, p1, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;

    if-eqz v0, :cond_0

    check-cast p1, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;

    iget-wide v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->timestamp:J

    iget-wide v2, p1, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->timestamp:J

    cmp-long v0, v0, v2

    if-nez v0, :cond_0

    iget v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->margin:F

    iget p1, p1, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->margin:F

    cmpl-float p1, v0, p1

    if-nez p1, :cond_0

    const/4 p1, 0x1

    return p1

    :cond_0
    const/4 p1, 0x0

    return p1
.end method

.method private synthetic $record$getFieldsAsObjects()[Ljava/lang/Object;
    .locals 3

    iget v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->margin:F

    invoke-static {v0}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v0

    iget-wide v1, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->timestamp:J

    invoke-static {v1, v2}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;

    move-result-object v1

    filled-new-array {v0, v1}, [Ljava/lang/Object;

    move-result-object v0

    return-object v0
.end method

.method private constructor <init>(FJ)V
    .locals 0
    .param p1, "margin"    # F
    .param p2, "timestamp"    # J
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x0,
            0x0
        }
        names = {
            "margin",
            "timestamp"
        }
    .end annotation

    .line 363
    invoke-direct {p0}, Lcom/android/tools/r8/RecordTag;-><init>()V

    iput p1, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->margin:F

    iput-wide p2, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->timestamp:J

    return-void
.end method

.method synthetic constructor <init>(FJLdev/gallerybridge/mediasync/AgentHook-IA;)V
    .locals 0

    invoke-direct {p0, p1, p2, p3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;-><init>(FJ)V

    return-void
.end method


# virtual methods
.method public final equals(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "o"    # Ljava/lang/Object;

    .line 363
    invoke-direct {p0, p1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->$record$equals(Ljava/lang/Object;)Z

    move-result v0

    return v0
.end method

.method public final hashCode()I
    .locals 3

    .line 363
    iget-wide v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->timestamp:J

    iget v2, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->margin:F

    invoke-static {v0, v1, v2}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast$$ExternalSyntheticRecord0;->m(JF)I

    move-result v0

    return v0
.end method

.method public margin()F
    .locals 1

    .line 363
    iget v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->margin:F

    return v0
.end method

.method public timestamp()J
    .locals 2

    .line 363
    iget-wide v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->timestamp:J

    return-wide v0
.end method

.method public final toString()Ljava/lang/String;
    .locals 3

    .line 363
    invoke-direct {p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;->$record$getFieldsAsObjects()[Ljava/lang/Object;

    move-result-object v0

    const-class v1, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;

    const-string v2, "margin;timestamp"

    invoke-static {v0, v1, v2}, Ldev/gallerybridge/mediasync/AgentHook$GateScope$$ExternalSyntheticRecord1;->m([Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

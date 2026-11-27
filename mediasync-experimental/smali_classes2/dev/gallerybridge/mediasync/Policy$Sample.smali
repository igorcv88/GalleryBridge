.class final Ldev/gallerybridge/mediasync/Policy$Sample;
.super Lcom/android/tools/r8/RecordTag;
.source "Policy.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Ldev/gallerybridge/mediasync/Policy;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x18
    name = "Sample"
.end annotation


# instance fields
.field private final ageMillis:J

.field private final battery:I

.field private final charging:Z

.field private final healthy:Z

.field private final powerSaver:Z

.field private final predicted:F

.field private final thermal:I


# direct methods
.method private synthetic $record$equals(Ljava/lang/Object;)Z
    .locals 4

    instance-of v0, p1, Ldev/gallerybridge/mediasync/Policy$Sample;

    if-eqz v0, :cond_0

    check-cast p1, Ldev/gallerybridge/mediasync/Policy$Sample;

    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->charging:Z

    iget-boolean v1, p1, Ldev/gallerybridge/mediasync/Policy$Sample;->charging:Z

    if-ne v0, v1, :cond_0

    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->healthy:Z

    iget-boolean v1, p1, Ldev/gallerybridge/mediasync/Policy$Sample;->healthy:Z

    if-ne v0, v1, :cond_0

    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->powerSaver:Z

    iget-boolean v1, p1, Ldev/gallerybridge/mediasync/Policy$Sample;->powerSaver:Z

    if-ne v0, v1, :cond_0

    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal:I

    iget v1, p1, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal:I

    if-ne v0, v1, :cond_0

    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->battery:I

    iget v1, p1, Ldev/gallerybridge/mediasync/Policy$Sample;->battery:I

    if-ne v0, v1, :cond_0

    iget-wide v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->ageMillis:J

    iget-wide v2, p1, Ldev/gallerybridge/mediasync/Policy$Sample;->ageMillis:J

    cmp-long v0, v0, v2

    if-nez v0, :cond_0

    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->predicted:F

    iget p1, p1, Ldev/gallerybridge/mediasync/Policy$Sample;->predicted:F

    cmpl-float p1, v0, p1

    if-nez p1, :cond_0

    const/4 p1, 0x1

    return p1

    :cond_0
    const/4 p1, 0x0

    return p1
.end method

.method private synthetic $record$getFieldsAsObjects()[Ljava/lang/Object;
    .locals 9

    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal:I

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->predicted:F

    invoke-static {v0}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v2

    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->battery:I

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->charging:Z

    invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v4

    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->healthy:Z

    invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v5

    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->powerSaver:Z

    invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v6

    iget-wide v7, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->ageMillis:J

    invoke-static {v7, v8}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;

    move-result-object v7

    filled-new-array/range {v1 .. v7}, [Ljava/lang/Object;

    move-result-object v0

    return-object v0
.end method

.method constructor <init>(IFIZZZJ)V
    .locals 0
    .param p1, "thermal"    # I
    .param p2, "predicted"    # F
    .param p3, "battery"    # I
    .param p4, "charging"    # Z
    .param p5, "healthy"    # Z
    .param p6, "powerSaver"    # Z
    .param p7, "ageMillis"    # J
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0
        }
        names = {
            "thermal",
            "predicted",
            "battery",
            "charging",
            "healthy",
            "powerSaver",
            "ageMillis"
        }
    .end annotation

    .line 13
    invoke-direct {p0}, Lcom/android/tools/r8/RecordTag;-><init>()V

    iput p1, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal:I

    iput p2, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->predicted:F

    iput p3, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->battery:I

    iput-boolean p4, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->charging:Z

    iput-boolean p5, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->healthy:Z

    iput-boolean p6, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->powerSaver:Z

    iput-wide p7, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->ageMillis:J

    return-void
.end method


# virtual methods
.method public ageMillis()J
    .locals 2

    .line 13
    iget-wide v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->ageMillis:J

    return-wide v0
.end method

.method public battery()I
    .locals 1

    .line 13
    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->battery:I

    return v0
.end method

.method public charging()Z
    .locals 1

    .line 13
    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->charging:Z

    return v0
.end method

.method public final equals(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "o"    # Ljava/lang/Object;

    .line 13
    invoke-direct {p0, p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->$record$equals(Ljava/lang/Object;)Z

    move-result v0

    return v0
.end method

.method public final hashCode()I
    .locals 8

    .line 13
    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->charging:Z

    iget-boolean v1, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->healthy:Z

    iget-boolean v2, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->powerSaver:Z

    iget v3, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal:I

    iget v4, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->battery:I

    iget-wide v5, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->ageMillis:J

    iget v7, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->predicted:F

    invoke-static/range {v0 .. v7}, Ldev/gallerybridge/mediasync/Policy$Sample$$ExternalSyntheticRecord0;->m(ZZZIIJF)I

    move-result v0

    return v0
.end method

.method public healthy()Z
    .locals 1

    .line 13
    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->healthy:Z

    return v0
.end method

.method public powerSaver()Z
    .locals 1

    .line 13
    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->powerSaver:Z

    return v0
.end method

.method public predicted()F
    .locals 1

    .line 13
    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->predicted:F

    return v0
.end method

.method public thermal()I
    .locals 1

    .line 13
    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal:I

    return v0
.end method

.method public final toString()Ljava/lang/String;
    .locals 3

    .line 13
    invoke-direct {p0}, Ldev/gallerybridge/mediasync/Policy$Sample;->$record$getFieldsAsObjects()[Ljava/lang/Object;

    move-result-object v0

    const-class v1, Ldev/gallerybridge/mediasync/Policy$Sample;

    const-string v2, "thermal;predicted;battery;charging;healthy;powerSaver;ageMillis"

    invoke-static {v0, v1, v2}, Ldev/gallerybridge/mediasync/AgentHook$GateScope$$ExternalSyntheticRecord1;->m([Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

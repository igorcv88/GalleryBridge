.class final Ldev/gallerybridge/mediasync/Policy$Settings;
.super Lcom/android/tools/r8/RecordTag;
.source "Policy.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Ldev/gallerybridge/mediasync/Policy;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x18
    name = "Settings"
.end annotation


# instance fields
.field private final battery:Z

.field private final diagnostics:Z

.field private final limitPercent:I

.field private final minimumBattery:I

.field private final profile:Ldev/gallerybridge/mediasync/Policy$Profile;

.field private final thermal:Z


# direct methods
.method private synthetic $record$equals(Ljava/lang/Object;)Z
    .locals 2

    instance-of v0, p1, Ldev/gallerybridge/mediasync/Policy$Settings;

    if-eqz v0, :cond_0

    check-cast p1, Ldev/gallerybridge/mediasync/Policy$Settings;

    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->thermal:Z

    iget-boolean v1, p1, Ldev/gallerybridge/mediasync/Policy$Settings;->thermal:Z

    if-ne v0, v1, :cond_0

    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->battery:Z

    iget-boolean v1, p1, Ldev/gallerybridge/mediasync/Policy$Settings;->battery:Z

    if-ne v0, v1, :cond_0

    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->diagnostics:Z

    iget-boolean v1, p1, Ldev/gallerybridge/mediasync/Policy$Settings;->diagnostics:Z

    if-ne v0, v1, :cond_0

    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->limitPercent:I

    iget v1, p1, Ldev/gallerybridge/mediasync/Policy$Settings;->limitPercent:I

    if-ne v0, v1, :cond_0

    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->minimumBattery:I

    iget v1, p1, Ldev/gallerybridge/mediasync/Policy$Settings;->minimumBattery:I

    if-ne v0, v1, :cond_0

    iget-object v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->profile:Ldev/gallerybridge/mediasync/Policy$Profile;

    iget-object p1, p1, Ldev/gallerybridge/mediasync/Policy$Settings;->profile:Ldev/gallerybridge/mediasync/Policy$Profile;

    invoke-static {v0, p1}, Ljava/util/Objects;->equals(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_0

    const/4 p1, 0x1

    return p1

    :cond_0
    const/4 p1, 0x0

    return p1
.end method

.method private synthetic $record$getFieldsAsObjects()[Ljava/lang/Object;
    .locals 6

    iget-object v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->profile:Ldev/gallerybridge/mediasync/Policy$Profile;

    iget-boolean v1, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->thermal:Z

    invoke-static {v1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v1

    iget-boolean v2, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->battery:Z

    invoke-static {v2}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v2

    iget v3, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->limitPercent:I

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    iget v4, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->minimumBattery:I

    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v4

    iget-boolean v5, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->diagnostics:Z

    invoke-static {v5}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v5

    filled-new-array/range {v0 .. v5}, [Ljava/lang/Object;

    move-result-object v0

    return-object v0
.end method

.method constructor <init>(Ldev/gallerybridge/mediasync/Policy$Profile;ZZIIZ)V
    .locals 0
    .param p1, "profile"    # Ldev/gallerybridge/mediasync/Policy$Profile;
    .param p2, "thermal"    # Z
    .param p3, "battery"    # Z
    .param p4, "limitPercent"    # I
    .param p5, "minimumBattery"    # I
    .param p6, "diagnostics"    # Z
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0
        }
        names = {
            "profile",
            "thermal",
            "battery",
            "limitPercent",
            "minimumBattery",
            "diagnostics"
        }
    .end annotation

    .line 8
    invoke-direct {p0}, Lcom/android/tools/r8/RecordTag;-><init>()V

    iput-object p1, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->profile:Ldev/gallerybridge/mediasync/Policy$Profile;

    iput-boolean p2, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->thermal:Z

    iput-boolean p3, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->battery:Z

    iput p4, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->limitPercent:I

    iput p5, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->minimumBattery:I

    iput-boolean p6, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->diagnostics:Z

    return-void
.end method


# virtual methods
.method public battery()Z
    .locals 1

    .line 8
    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->battery:Z

    return v0
.end method

.method public diagnostics()Z
    .locals 1

    .line 8
    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->diagnostics:Z

    return v0
.end method

.method public final equals(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "o"    # Ljava/lang/Object;

    .line 8
    invoke-direct {p0, p1}, Ldev/gallerybridge/mediasync/Policy$Settings;->$record$equals(Ljava/lang/Object;)Z

    move-result v0

    return v0
.end method

.method public final hashCode()I
    .locals 6

    .line 8
    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->thermal:Z

    iget-boolean v1, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->battery:Z

    iget-boolean v2, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->diagnostics:Z

    iget v3, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->limitPercent:I

    iget v4, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->minimumBattery:I

    iget-object v5, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->profile:Ldev/gallerybridge/mediasync/Policy$Profile;

    invoke-static/range {v0 .. v5}, Ldev/gallerybridge/mediasync/Policy$Settings$$ExternalSyntheticRecord0;->m(ZZZIILjava/lang/Object;)I

    move-result v0

    return v0
.end method

.method public limitPercent()I
    .locals 1

    .line 8
    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->limitPercent:I

    return v0
.end method

.method public minimumBattery()I
    .locals 1

    .line 8
    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->minimumBattery:I

    return v0
.end method

.method public profile()Ldev/gallerybridge/mediasync/Policy$Profile;
    .locals 1

    .line 8
    iget-object v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->profile:Ldev/gallerybridge/mediasync/Policy$Profile;

    return-object v0
.end method

.method public thermal()Z
    .locals 1

    .line 8
    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->thermal:Z

    return v0
.end method

.method public final toString()Ljava/lang/String;
    .locals 3

    .line 8
    invoke-direct {p0}, Ldev/gallerybridge/mediasync/Policy$Settings;->$record$getFieldsAsObjects()[Ljava/lang/Object;

    move-result-object v0

    const-class v1, Ldev/gallerybridge/mediasync/Policy$Settings;

    const-string v2, "profile;thermal;battery;limitPercent;minimumBattery;diagnostics"

    invoke-static {v0, v1, v2}, Ldev/gallerybridge/mediasync/AgentHook$GateScope$$ExternalSyntheticRecord1;->m([Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method valid()Z
    .locals 2

    .line 10
    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->limitPercent:I

    const/16 v1, 0x37

    if-lt v0, v1, :cond_0

    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->limitPercent:I

    const/16 v1, 0x4b

    if-gt v0, v1, :cond_0

    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->minimumBattery:I

    const/16 v1, 0xa

    if-lt v0, v1, :cond_0

    iget v0, p0, Ldev/gallerybridge/mediasync/Policy$Settings;->minimumBattery:I

    const/16 v1, 0x1e

    if-gt v0, v1, :cond_0

    const/4 v0, 0x1

    goto :goto_0

    :cond_0
    const/4 v0, 0x0

    :goto_0
    return v0
.end method

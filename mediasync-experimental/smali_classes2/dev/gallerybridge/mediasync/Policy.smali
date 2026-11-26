.class final Ldev/gallerybridge/mediasync/Policy;
.super Ljava/lang/Object;
.source "Policy.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Ldev/gallerybridge/mediasync/Policy$Settings;,
        Ldev/gallerybridge/mediasync/Policy$Profile;,
        Ldev/gallerybridge/mediasync/Policy$Sample;
    }
.end annotation


# static fields
.field static final MODERATE:I = 0x2

.field static final SEVERE:I = 0x3


# direct methods
.method constructor <init>()V
    .locals 0

    .line 3
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method static allowBattery(Ldev/gallerybridge/mediasync/Policy$Settings;Ldev/gallerybridge/mediasync/Policy$Sample;Z)Z
    .locals 3
    .param p0, "config"    # Ldev/gallerybridge/mediasync/Policy$Settings;
    .param p1, "state"    # Ldev/gallerybridge/mediasync/Policy$Sample;
    .param p2, "latched"    # Z

    .line 23
    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/Policy$Settings;->profile()Ldev/gallerybridge/mediasync/Policy$Profile;

    move-result-object v0

    sget-object v1, Ldev/gallerybridge/mediasync/Policy$Profile;->CONSERVATIVE:Ldev/gallerybridge/mediasync/Policy$Profile;

    if-ne v0, v1, :cond_0

    const/16 v0, 0xc

    goto :goto_0

    :cond_0
    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/Policy$Settings;->minimumBattery()I

    move-result v0

    .line 24
    .local v0, "minimum":I
    :goto_0
    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/Policy$Settings;->profile()Ldev/gallerybridge/mediasync/Policy$Profile;

    move-result-object v1

    sget-object v2, Ldev/gallerybridge/mediasync/Policy$Profile;->OFF:Ldev/gallerybridge/mediasync/Policy$Profile;

    if-eq v1, v2, :cond_1

    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/Policy$Settings;->battery()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-static {p0, p1, p2}, Ldev/gallerybridge/mediasync/Policy;->compatible(Ldev/gallerybridge/mediasync/Policy$Settings;Ldev/gallerybridge/mediasync/Policy$Sample;Z)Z

    move-result v1

    if-eqz v1, :cond_1

    .line 25
    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->charging()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->healthy()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->battery()I

    move-result v1

    if-lt v1, v0, :cond_1

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->battery()I

    move-result v1

    const/16 v2, 0x64

    if-gt v1, v2, :cond_1

    const/4 v1, 0x1

    goto :goto_1

    :cond_1
    const/4 v1, 0x0

    .line 24
    :goto_1
    return v1
.end method

.method static allowThermal(Ldev/gallerybridge/mediasync/Policy$Settings;Ldev/gallerybridge/mediasync/Policy$Sample;Z)Z
    .locals 3
    .param p0, "config"    # Ldev/gallerybridge/mediasync/Policy$Settings;
    .param p1, "state"    # Ldev/gallerybridge/mediasync/Policy$Sample;
    .param p2, "latched"    # Z

    .line 17
    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/Policy$Settings;->profile()Ldev/gallerybridge/mediasync/Policy$Profile;

    move-result-object v0

    sget-object v1, Ldev/gallerybridge/mediasync/Policy$Profile;->CUSTOM:Ldev/gallerybridge/mediasync/Policy$Profile;

    if-ne v0, v1, :cond_0

    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/Policy$Settings;->thermal()Z

    move-result v0

    if-eqz v0, :cond_0

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal()I

    move-result v0

    const/4 v1, 0x2

    if-ne v0, v1, :cond_0

    .line 18
    invoke-static {p0, p1, p2}, Ldev/gallerybridge/mediasync/Policy;->compatible(Ldev/gallerybridge/mediasync/Policy$Settings;Ldev/gallerybridge/mediasync/Policy$Sample;Z)Z

    move-result v0

    if-eqz v0, :cond_0

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->charging()Z

    move-result v0

    if-eqz v0, :cond_0

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->healthy()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 19
    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->battery()I

    move-result v0

    const/16 v1, 0x14

    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/Policy$Settings;->minimumBattery()I

    move-result v2

    invoke-static {v1, v2}, Ljava/lang/Math;->max(II)I

    move-result v1

    if-lt v0, v1, :cond_0

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->battery()I

    move-result v0

    const/16 v1, 0x64

    if-gt v0, v1, :cond_0

    const/4 v0, 0x1

    goto :goto_0

    :cond_0
    const/4 v0, 0x0

    .line 17
    :goto_0
    return v0
.end method

.method private static compatible(Ldev/gallerybridge/mediasync/Policy$Settings;Ldev/gallerybridge/mediasync/Policy$Sample;Z)Z
    .locals 5
    .param p0, "config"    # Ldev/gallerybridge/mediasync/Policy$Settings;
    .param p1, "state"    # Ldev/gallerybridge/mediasync/Policy$Sample;
    .param p2, "latched"    # Z

    .line 29
    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/Policy$Settings;->profile()Ldev/gallerybridge/mediasync/Policy$Profile;

    move-result-object v0

    sget-object v1, Ldev/gallerybridge/mediasync/Policy$Profile;->CONSERVATIVE:Ldev/gallerybridge/mediasync/Policy$Profile;

    if-ne v0, v1, :cond_0

    const v0, 0x3f19999a    # 0.6f

    goto :goto_0

    :cond_0
    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/Policy$Settings;->limitPercent()I

    move-result v0

    int-to-float v0, v0

    const/high16 v1, 0x42c80000    # 100.0f

    div-float/2addr v0, v1

    .line 30
    .local v0, "limit":F
    :goto_0
    if-nez p2, :cond_1

    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/Policy$Settings;->valid()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal()I

    move-result v1

    if-ltz v1, :cond_1

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal()I

    move-result v1

    const/4 v2, 0x3

    if-ge v1, v2, :cond_1

    .line 31
    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->predicted()F

    move-result v1

    invoke-static {v1}, Ljava/lang/Float;->isFinite(F)Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->predicted()F

    move-result v1

    const/4 v2, 0x0

    cmpl-float v1, v1, v2

    if-ltz v1, :cond_1

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->predicted()F

    move-result v1

    cmpg-float v1, v1, v0

    if-gtz v1, :cond_1

    .line 32
    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->powerSaver()Z

    move-result v1

    if-nez v1, :cond_1

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->ageMillis()J

    move-result-wide v1

    const-wide/16 v3, 0x0

    cmp-long v1, v1, v3

    if-ltz v1, :cond_1

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/Policy$Sample;->ageMillis()J

    move-result-wide v1

    const-wide/16 v3, 0xdac

    cmp-long v1, v1, v3

    if-gtz v1, :cond_1

    const/4 v1, 0x1

    goto :goto_1

    :cond_1
    const/4 v1, 0x0

    .line 30
    :goto_1
    return v1
.end method

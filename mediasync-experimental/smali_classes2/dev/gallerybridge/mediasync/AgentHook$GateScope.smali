.class final Ldev/gallerybridge/mediasync/AgentHook$GateScope;
.super Lcom/android/tools/r8/RecordTag;
.source "AgentHook.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Ldev/gallerybridge/mediasync/AgentHook;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1a
    name = "GateScope"
.end annotation


# instance fields
.field private final prior:Ldev/gallerybridge/mediasync/AgentHook$GateScope;

.field private final raised:Z


# direct methods
.method private synthetic $record$equals(Ljava/lang/Object;)Z
    .locals 2

    instance-of v0, p1, Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    if-eqz v0, :cond_0

    check-cast p1, Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->raised:Z

    iget-boolean v1, p1, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->raised:Z

    if-ne v0, v1, :cond_0

    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->prior:Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    iget-object p1, p1, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->prior:Ldev/gallerybridge/mediasync/AgentHook$GateScope;

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
    .locals 2

    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->raised:Z

    invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v0

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->prior:Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    filled-new-array {v0, v1}, [Ljava/lang/Object;

    move-result-object v0

    return-object v0
.end method

.method private constructor <init>(ZLdev/gallerybridge/mediasync/AgentHook$GateScope;)V
    .locals 0
    .param p1, "raised"    # Z
    .param p2, "prior"    # Ldev/gallerybridge/mediasync/AgentHook$GateScope;
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x0,
            0x0
        }
        names = {
            "raised",
            "prior"
        }
    .end annotation

    .line 348
    invoke-direct {p0}, Lcom/android/tools/r8/RecordTag;-><init>()V

    iput-boolean p1, p0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->raised:Z

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->prior:Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    return-void
.end method

.method synthetic constructor <init>(ZLdev/gallerybridge/mediasync/AgentHook$GateScope;Ldev/gallerybridge/mediasync/AgentHook-IA;)V
    .locals 0

    invoke-direct {p0, p1, p2}, Ldev/gallerybridge/mediasync/AgentHook$GateScope;-><init>(ZLdev/gallerybridge/mediasync/AgentHook$GateScope;)V

    return-void
.end method


# virtual methods
.method public final equals(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "o"    # Ljava/lang/Object;

    .line 348
    invoke-direct {p0, p1}, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->$record$equals(Ljava/lang/Object;)Z

    move-result v0

    return v0
.end method

.method public final hashCode()I
    .locals 2

    .line 348
    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->raised:Z

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->prior:Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    invoke-static {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook$GateScope$$ExternalSyntheticRecord0;->m(ZLjava/lang/Object;)I

    move-result v0

    return v0
.end method

.method public prior()Ldev/gallerybridge/mediasync/AgentHook$GateScope;
    .locals 1

    .line 348
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->prior:Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    return-object v0
.end method

.method public raised()Z
    .locals 1

    .line 348
    iget-boolean v0, p0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->raised:Z

    return v0
.end method

.method public final toString()Ljava/lang/String;
    .locals 3

    .line 348
    invoke-direct {p0}, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->$record$getFieldsAsObjects()[Ljava/lang/Object;

    move-result-object v0

    const-class v1, Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    const-string v2, "raised;prior"

    invoke-static {v0, v1, v2}, Ldev/gallerybridge/mediasync/AgentHook$GateScope$$ExternalSyntheticRecord1;->m([Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

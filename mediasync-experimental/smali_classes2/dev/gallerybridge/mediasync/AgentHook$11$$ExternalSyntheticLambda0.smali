.class public final synthetic Ldev/gallerybridge/mediasync/AgentHook$11$$ExternalSyntheticLambda0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ldev/gallerybridge/mediasync/AgentHook$Checked;


# instance fields
.field public final synthetic f$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

.field public final synthetic f$1:Ljava/time/Duration;


# direct methods
.method public synthetic constructor <init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/time/Duration;)V
    .locals 0

    .line 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$11$$ExternalSyntheticLambda0;->f$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$11$$ExternalSyntheticLambda0;->f$1:Ljava/time/Duration;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 2

    .line 0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$11$$ExternalSyntheticLambda0;->f$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$11$$ExternalSyntheticLambda0;->f$1:Ljava/time/Duration;

    invoke-static {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook$11;->lambda$beforeHookedMethod$0(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/time/Duration;)V

    return-void
.end method

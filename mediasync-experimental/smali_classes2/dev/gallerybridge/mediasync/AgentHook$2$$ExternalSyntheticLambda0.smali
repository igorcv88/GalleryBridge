.class public final synthetic Ldev/gallerybridge/mediasync/AgentHook$2$$ExternalSyntheticLambda0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ldev/gallerybridge/mediasync/AgentHook$Checked;


# instance fields
.field public final synthetic f$0:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

.field public final synthetic f$1:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;


# direct methods
.method public synthetic constructor <init>(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V
    .locals 0

    .line 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$2$$ExternalSyntheticLambda0;->f$0:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$2$$ExternalSyntheticLambda0;->f$1:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 2

    .line 0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$2$$ExternalSyntheticLambda0;->f$0:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$2$$ExternalSyntheticLambda0;->f$1:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook$2;->lambda$afterHookedMethod$0(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V

    return-void
.end method

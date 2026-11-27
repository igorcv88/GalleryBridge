.class public final synthetic Ldev/gallerybridge/mediasync/AgentHook$8$$ExternalSyntheticLambda0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ldev/gallerybridge/mediasync/AgentHook$Checked;


# instance fields
.field public final synthetic f$0:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

.field public final synthetic f$1:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

.field public final synthetic f$2:Ljava/lang/reflect/Field;


# direct methods
.method public synthetic constructor <init>(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/reflect/Field;)V
    .locals 0

    .line 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$8$$ExternalSyntheticLambda0;->f$0:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$8$$ExternalSyntheticLambda0;->f$1:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iput-object p3, p0, Ldev/gallerybridge/mediasync/AgentHook$8$$ExternalSyntheticLambda0;->f$2:Ljava/lang/reflect/Field;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 3

    .line 0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$8$$ExternalSyntheticLambda0;->f$0:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$8$$ExternalSyntheticLambda0;->f$1:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$8$$ExternalSyntheticLambda0;->f$2:Ljava/lang/reflect/Field;

    invoke-static {v0, v1, v2}, Ldev/gallerybridge/mediasync/AgentHook$8;->lambda$beforeHookedMethod$0(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/reflect/Field;)V

    return-void
.end method

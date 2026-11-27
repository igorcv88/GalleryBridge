.class public final synthetic Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ldev/gallerybridge/mediasync/AgentHook$Checked;


# instance fields
.field public final synthetic f$0:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

.field public final synthetic f$1:Ljava/lang/Class;

.field public final synthetic f$2:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

.field public final synthetic f$3:Ljava/lang/reflect/Method;

.field public final synthetic f$4:Ljava/lang/reflect/Method;


# direct methods
.method public synthetic constructor <init>(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ljava/lang/Class;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V
    .locals 0

    .line 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;->f$0:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;->f$1:Ljava/lang/Class;

    iput-object p3, p0, Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;->f$2:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iput-object p4, p0, Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;->f$3:Ljava/lang/reflect/Method;

    iput-object p5, p0, Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;->f$4:Ljava/lang/reflect/Method;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 5

    .line 0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;->f$0:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;->f$1:Ljava/lang/Class;

    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;->f$2:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v3, p0, Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;->f$3:Ljava/lang/reflect/Method;

    iget-object v4, p0, Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;->f$4:Ljava/lang/reflect/Method;

    invoke-static {v0, v1, v2, v3, v4}, Ldev/gallerybridge/mediasync/AgentHook$9;->lambda$beforeHookedMethod$0(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ljava/lang/Class;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V

    return-void
.end method

.class public final synthetic Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ldev/gallerybridge/mediasync/AgentHook$Checked;


# instance fields
.field public final synthetic f$0:Ljava/lang/reflect/Field;

.field public final synthetic f$1:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

.field public final synthetic f$2:Ljava/lang/reflect/Field;

.field public final synthetic f$3:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

.field public final synthetic f$4:Ljava/lang/ThreadLocal;


# direct methods
.method public synthetic constructor <init>(Ljava/lang/reflect/Field;Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ljava/lang/reflect/Field;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/ThreadLocal;)V
    .locals 0

    .line 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;->f$0:Ljava/lang/reflect/Field;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;->f$1:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    iput-object p3, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;->f$2:Ljava/lang/reflect/Field;

    iput-object p4, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;->f$3:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iput-object p5, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;->f$4:Ljava/lang/ThreadLocal;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 5

    .line 0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;->f$0:Ljava/lang/reflect/Field;

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;->f$1:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;->f$2:Ljava/lang/reflect/Field;

    iget-object v3, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;->f$3:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v4, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;->f$4:Ljava/lang/ThreadLocal;

    invoke-static {v0, v1, v2, v3, v4}, Ldev/gallerybridge/mediasync/AgentHook$6;->lambda$beforeHookedMethod$0(Ljava/lang/reflect/Field;Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ljava/lang/reflect/Field;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/ThreadLocal;)V

    return-void
.end method

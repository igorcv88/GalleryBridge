.class public final synthetic Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ldev/gallerybridge/mediasync/AgentHook$Checked;


# instance fields
.field public final synthetic f$0:Ljava/lang/reflect/Field;

.field public final synthetic f$1:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;


# direct methods
.method public synthetic constructor <init>(Ljava/lang/reflect/Field;Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 0

    .line 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda0;->f$0:Ljava/lang/reflect/Field;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda0;->f$1:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 2

    .line 0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda0;->f$0:Ljava/lang/reflect/Field;

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda0;->f$1:Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    invoke-static {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook$6;->lambda$afterHookedMethod$1(Ljava/lang/reflect/Field;Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V

    return-void
.end method

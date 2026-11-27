.class Ldev/gallerybridge/mediasync/AgentHook$4;
.super Lde/robv/android/xposed/XC_MethodHook;
.source "AgentHook.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Ldev/gallerybridge/mediasync/AgentHook;->installHooks(Landroid/content/Context;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Field;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/Class;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;Ljava/lang/reflect/Constructor;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;


# direct methods
.method constructor <init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V
    .locals 0

    .line 211
    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$4;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-direct {p0}, Lde/robv/android/xposed/XC_MethodHook;-><init>()V

    return-void
.end method


# virtual methods
.method protected afterHookedMethod(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 3
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    .line 213
    invoke-virtual {p1}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->hasThrowable()Z

    move-result v0

    if-nez v0, :cond_2

    invoke-virtual {p1}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->getResult()Ljava/lang/Object;

    move-result-object v0

    if-nez v0, :cond_0

    goto :goto_0

    .line 214
    :cond_0
    invoke-virtual {p1}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->getResult()Ljava/lang/Object;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;

    move-result-object v0

    const-string v1, "com.samsung.android.agent.storage.domain.exception.SyncConditionFailureReason"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_1

    .line 215
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$4;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-virtual {p1}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->getResult()Ljava/lang/Object;

    move-result-object v1

    invoke-static {v1}, Ldev/gallerybridge/mediasync/AgentHook;->-$$Nest$smenumName(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    const-string v2, "precondition"

    invoke-virtual {v0, v2, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->event(Ljava/lang/String;Ljava/lang/String;)V

    .line 217
    :cond_1
    return-void

    .line 213
    :cond_2
    :goto_0
    return-void
.end method

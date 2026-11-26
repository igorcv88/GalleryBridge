.class Ldev/gallerybridge/mediasync/AgentHook$7;
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
.field final synthetic val$scope:Ljava/lang/ThreadLocal;

.field final synthetic val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;


# direct methods
.method constructor <init>(Ljava/lang/ThreadLocal;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V
    .locals 0

    .line 254
    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$7;->val$scope:Ljava/lang/ThreadLocal;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$7;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-direct {p0}, Lde/robv/android/xposed/XC_MethodHook;-><init>()V

    return-void
.end method


# virtual methods
.method protected afterHookedMethod(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 2
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    .line 263
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$7;->val$scope:Ljava/lang/ThreadLocal;

    invoke-virtual {v0}, Ljava/lang/ThreadLocal;->get()Ljava/lang/Object;

    move-result-object v0

    if-eqz v0, :cond_0

    invoke-virtual {p1}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->hasThrowable()Z

    move-result v0

    if-nez v0, :cond_0

    iget-object v0, p1, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->args:[Ljava/lang/Object;

    const/4 v1, 0x0

    aget-object v0, v0, v1

    check-cast v0, Ljava/lang/Integer;

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v0

    const/4 v1, 0x2

    if-ne v0, v1, :cond_0

    .line 264
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$7;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v1, p1, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->thisObject:Ljava/lang/Object;

    invoke-virtual {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->monitor(Ljava/lang/Object;)V

    .line 266
    :cond_0
    return-void
.end method

.method protected beforeHookedMethod(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 5
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    .line 256
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$7;->val$scope:Ljava/lang/ThreadLocal;

    invoke-virtual {v0}, Ljava/lang/ThreadLocal;->get()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    .line 257
    .local v0, "frame":Ldev/gallerybridge/mediasync/AgentHook$GateScope;
    if-nez v0, :cond_0

    return-void

    .line 258
    :cond_0
    iget-object v1, p1, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->args:[Ljava/lang/Object;

    const/4 v2, 0x0

    aget-object v1, v1, v2

    check-cast v1, Ljava/lang/Integer;

    invoke-virtual {v1}, Ljava/lang/Integer;->intValue()I

    move-result v1

    .line 259
    .local v1, "limit":I
    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->raised()Z

    move-result v3

    if-eqz v3, :cond_1

    const/4 v3, 0x3

    if-ne v1, v3, :cond_1

    iget-object v3, p1, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->args:[Ljava/lang/Object;

    const/4 v4, 0x2

    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v4

    aput-object v4, v3, v2

    .line 260
    :cond_1
    return-void
.end method

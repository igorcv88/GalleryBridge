.class Ldev/gallerybridge/mediasync/AgentHook$9;
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
.field final synthetic val$code:Ljava/lang/reflect/Method;

.field final synthetic val$rpc:Ljava/lang/Class;

.field final synthetic val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

.field final synthetic val$status:Ljava/lang/reflect/Method;


# direct methods
.method constructor <init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/Class;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V
    .locals 0

    .line 286
    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$9;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$9;->val$rpc:Ljava/lang/Class;

    iput-object p3, p0, Ldev/gallerybridge/mediasync/AgentHook$9;->val$code:Ljava/lang/reflect/Method;

    iput-object p4, p0, Ldev/gallerybridge/mediasync/AgentHook$9;->val$status:Ljava/lang/reflect/Method;

    invoke-direct {p0}, Lde/robv/android/xposed/XC_MethodHook;-><init>()V

    return-void
.end method

.method static synthetic lambda$beforeHookedMethod$0(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ljava/lang/Class;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V
    .locals 6
    .param p0, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;
    .param p1, "rpc"    # Ljava/lang/Class;
    .param p2, "state"    # Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;
    .param p3, "code"    # Ljava/lang/reflect/Method;
    .param p4, "status"    # Ljava/lang/reflect/Method;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 290
    iget-object v0, p0, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->args:[Ljava/lang/Object;

    const/4 v1, 0x0

    aget-object v0, v0, v1

    check-cast v0, Ljava/lang/Throwable;

    .line 291
    .local v0, "cause":Ljava/lang/Throwable;
    const/4 v2, 0x0

    .local v2, "depth":I
    :goto_0
    const/4 v3, 0x6

    const-string v4, "scheduler_failure"

    if-ge v2, v3, :cond_2

    if-eqz v0, :cond_2

    .line 292
    invoke-virtual {p1, v0}, Ljava/lang/Class;->isInstance(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_0

    .line 293
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "rpc="

    invoke-virtual {v3, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    new-array v5, v1, [Ljava/lang/Object;

    invoke-virtual {p4, v0, v5}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v5

    new-array v1, v1, [Ljava/lang/Object;

    invoke-virtual {p3, v5, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v1

    invoke-static {v1}, Ldev/gallerybridge/mediasync/AgentHook;->-$$Nest$smenumName(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v3, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {p2, v4, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->event(Ljava/lang/String;Ljava/lang/String;)V

    .line 294
    return-void

    .line 296
    :cond_0
    invoke-virtual {v0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Class;->getName()Ljava/lang/String;

    move-result-object v3

    const-string v5, "com.google.android.gms.internal.media_sync.zzzp"

    invoke-virtual {v3, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_1

    .line 297
    const-string v1, "sdk_thermal_cancel"

    invoke-virtual {p2, v4, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->event(Ljava/lang/String;Ljava/lang/String;)V

    .line 298
    return-void

    .line 291
    :cond_1
    add-int/lit8 v2, v2, 0x1

    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;

    move-result-object v0

    goto :goto_0

    .line 301
    .end local v2    # "depth":I
    :cond_2
    const-string v1, "cause_unknown"

    invoke-virtual {p2, v4, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->event(Ljava/lang/String;Ljava/lang/String;)V

    .line 302
    return-void
.end method


# virtual methods
.method protected beforeHookedMethod(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 7
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    .line 288
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$9;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Ldev/gallerybridge/mediasync/Policy$Settings;

    move-result-object v0

    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/Policy$Settings;->diagnostics()Z

    move-result v0

    if-nez v0, :cond_0

    return-void

    .line 289
    :cond_0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$9;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v3, p0, Ldev/gallerybridge/mediasync/AgentHook$9;->val$rpc:Ljava/lang/Class;

    iget-object v4, p0, Ldev/gallerybridge/mediasync/AgentHook$9;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v5, p0, Ldev/gallerybridge/mediasync/AgentHook$9;->val$code:Ljava/lang/reflect/Method;

    iget-object v6, p0, Ldev/gallerybridge/mediasync/AgentHook$9;->val$status:Ljava/lang/reflect/Method;

    new-instance v1, Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;

    move-object v2, p1

    .end local p1    # "p":Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;
    .local v2, "p":Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;
    invoke-direct/range {v1 .. v6}, Ldev/gallerybridge/mediasync/AgentHook$9$$ExternalSyntheticLambda0;-><init>(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ljava/lang/Class;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V

    invoke-virtual {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->safely(Ldev/gallerybridge/mediasync/AgentHook$Checked;)V

    .line 303
    return-void
.end method

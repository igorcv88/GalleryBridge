.class Ldev/gallerybridge/mediasync/AgentHook$3;
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

    .line 196
    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$3;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-direct {p0}, Lde/robv/android/xposed/XC_MethodHook;-><init>()V

    return-void
.end method

.method static synthetic lambda$afterHookedMethod$0(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 6
    .param p0, "state"    # Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 200
    const-string v0, "com.samsung.android.agent.storage.domain.usecase.precondition.FindUnmetDeviceConditionUseCase"

    const-string v1, "invoke"

    invoke-static {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook;->-$$Nest$smcaller(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v0

    .line 201
    .local v0, "scoped":Z
    invoke-static {p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Ldev/gallerybridge/mediasync/Policy$Settings;

    move-result-object v1

    invoke-virtual {v1}, Ldev/gallerybridge/mediasync/Policy$Settings;->diagnostics()Z

    move-result v1

    if-nez v1, :cond_1

    invoke-static {p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Ldev/gallerybridge/mediasync/Policy$Settings;

    move-result-object v1

    .line 202
    invoke-virtual {v1}, Ldev/gallerybridge/mediasync/Policy$Settings;->profile()Ldev/gallerybridge/mediasync/Policy$Profile;

    move-result-object v1

    sget-object v2, Ldev/gallerybridge/mediasync/Policy$Profile;->OFF:Ldev/gallerybridge/mediasync/Policy$Profile;

    if-eq v1, v2, :cond_0

    invoke-static {p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Ldev/gallerybridge/mediasync/Policy$Settings;

    move-result-object v1

    invoke-virtual {v1}, Ldev/gallerybridge/mediasync/Policy$Settings;->battery()Z

    move-result v1

    if-eqz v1, :cond_0

    if-nez v0, :cond_1

    :cond_0
    return-void

    .line 203
    :cond_1
    invoke-virtual {p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->sample()Ldev/gallerybridge/mediasync/Policy$Sample;

    move-result-object v1

    .line 204
    .local v1, "sample":Ldev/gallerybridge/mediasync/Policy$Sample;
    const/4 v2, 0x0

    if-eqz v0, :cond_2

    invoke-static {p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Ldev/gallerybridge/mediasync/Policy$Settings;

    move-result-object v3

    invoke-static {p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetlatched(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Z

    move-result v4

    invoke-static {v3, v1, v4}, Ldev/gallerybridge/mediasync/Policy;->allowBattery(Ldev/gallerybridge/mediasync/Policy$Settings;Ldev/gallerybridge/mediasync/Policy$Sample;Z)Z

    move-result v3

    if-eqz v3, :cond_2

    const/4 v3, 0x1

    goto :goto_0

    :cond_2
    move v3, v2

    .line 205
    .local v3, "allowed":Z
    :goto_0
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "before=blocked; after="

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    if-eqz v3, :cond_3

    const-string v5, "allowed"

    goto :goto_1

    :cond_3
    const-string v5, "blocked"

    :goto_1
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    const-string v5, "; charging="

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    .line 206
    invoke-virtual {v1}, Ldev/gallerybridge/mediasync/Policy$Sample;->charging()Z

    move-result v5

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v4

    const-string v5, "; scoped="

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4, v0}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    .line 205
    const-string v5, "battery_samsung"

    invoke-virtual {p0, v5, v4}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->event(Ljava/lang/String;Ljava/lang/String;)V

    .line 207
    if-eqz v3, :cond_4

    invoke-static {v2}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v2

    invoke-virtual {p1, v2}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->setResult(Ljava/lang/Object;)V

    .line 208
    :cond_4
    return-void
.end method


# virtual methods
.method protected afterHookedMethod(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 3
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    .line 198
    invoke-virtual {p1}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->hasThrowable()Z

    move-result v0

    if-nez v0, :cond_1

    sget-object v0, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;

    invoke-virtual {p1}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->getResult()Ljava/lang/Object;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/Boolean;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_0

    goto :goto_0

    .line 199
    :cond_0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$3;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$3;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    new-instance v2, Ldev/gallerybridge/mediasync/AgentHook$3$$ExternalSyntheticLambda0;

    invoke-direct {v2, v1, p1}, Ldev/gallerybridge/mediasync/AgentHook$3$$ExternalSyntheticLambda0;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V

    invoke-virtual {v0, v2}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->safely(Ldev/gallerybridge/mediasync/AgentHook$Checked;)V

    .line 209
    return-void

    .line 198
    :cond_1
    :goto_0
    return-void
.end method

.class Ldev/gallerybridge/mediasync/AgentHook$2;
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

    .line 182
    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$2;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-direct {p0}, Lde/robv/android/xposed/XC_MethodHook;-><init>()V

    return-void
.end method

.method static synthetic lambda$afterHookedMethod$0(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V
    .locals 7
    .param p0, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;
    .param p1, "state"    # Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 186
    iget-object v0, p0, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->args:[Ljava/lang/Object;

    const/4 v1, 0x0

    aget-object v0, v0, v1

    invoke-static {v0}, Ldev/gallerybridge/mediasync/AgentHook;->-$$Nest$smenumName(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v0

    .line 187
    .local v0, "reported":Ljava/lang/String;
    const-string v2, "com.samsung.android.agent.storage.domain.usecase.precondition.FindUnmetDeviceConditionUseCase"

    const-string v3, "invoke"

    invoke-static {v2, v3}, Ldev/gallerybridge/mediasync/AgentHook;->-$$Nest$smcaller(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v2

    const/4 v3, 0x1

    if-nez v2, :cond_1

    const-string v2, "com.samsung.android.agent.storage.work.ImportWorker"

    const-string v4, "cancelIfThermalBlockedAtStart"

    invoke-static {v2, v4}, Ldev/gallerybridge/mediasync/AgentHook;->-$$Nest$smcaller(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v2

    if-eqz v2, :cond_0

    goto :goto_0

    :cond_0
    move v2, v1

    goto :goto_1

    :cond_1
    :goto_0
    move v2, v3

    .line 188
    .local v2, "scoped":Z
    :goto_1
    if-eqz v2, :cond_2

    const-string v4, "MODERATE"

    invoke-virtual {v4, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_2

    invoke-static {p1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Ldev/gallerybridge/mediasync/Policy$Settings;

    move-result-object v4

    .line 189
    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->sample()Ldev/gallerybridge/mediasync/Policy$Sample;

    move-result-object v5

    invoke-static {p1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetlatched(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Z

    move-result v6

    invoke-static {v4, v5, v6}, Ldev/gallerybridge/mediasync/Policy;->allowThermal(Ldev/gallerybridge/mediasync/Policy$Settings;Ldev/gallerybridge/mediasync/Policy$Sample;Z)Z

    move-result v4

    if-eqz v4, :cond_2

    goto :goto_2

    :cond_2
    move v3, v1

    .line 190
    .local v3, "allowed":Z
    :goto_2
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "observed="

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    const-string v5, "; before=blocked; after="

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    .line 191
    if-eqz v3, :cond_3

    const-string v5, "allowed"

    goto :goto_3

    :cond_3
    const-string v5, "blocked"

    :goto_3
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    const-string v5, "; scoped="

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4, v2}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    .line 190
    const-string v5, "thermal_samsung"

    invoke-virtual {p1, v5, v4}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->event(Ljava/lang/String;Ljava/lang/String;)V

    .line 192
    if-eqz v3, :cond_4

    invoke-static {v1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v1

    invoke-virtual {p0, v1}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->setResult(Ljava/lang/Object;)V

    .line 193
    :cond_4
    return-void
.end method


# virtual methods
.method protected afterHookedMethod(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 3
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    .line 184
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

    .line 185
    :cond_0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$2;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$2;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    new-instance v2, Ldev/gallerybridge/mediasync/AgentHook$2$$ExternalSyntheticLambda0;

    invoke-direct {v2, p1, v1}, Ldev/gallerybridge/mediasync/AgentHook$2$$ExternalSyntheticLambda0;-><init>(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V

    invoke-virtual {v0, v2}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->safely(Ldev/gallerybridge/mediasync/AgentHook$Checked;)V

    .line 194
    return-void

    .line 184
    :cond_1
    :goto_0
    return-void
.end method

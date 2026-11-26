.class Ldev/gallerybridge/mediasync/AgentHook$6;
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


# static fields
.field private static final EXTRA:Ljava/lang/String; = "gallerybridge.sdk.scope"


# instance fields
.field final synthetic val$scope:Ljava/lang/ThreadLocal;

.field final synthetic val$sdkLabel:Ljava/lang/reflect/Field;

.field final synthetic val$sdkLimit:Ljava/lang/reflect/Field;

.field final synthetic val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;


# direct methods
.method constructor <init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;Ljava/lang/ThreadLocal;)V
    .locals 0

    .line 224
    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$sdkLabel:Ljava/lang/reflect/Field;

    iput-object p3, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$sdkLimit:Ljava/lang/reflect/Field;

    iput-object p4, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$scope:Ljava/lang/ThreadLocal;

    invoke-direct {p0}, Lde/robv/android/xposed/XC_MethodHook;-><init>()V

    return-void
.end method

.method static synthetic lambda$afterHookedMethod$1(Ljava/lang/reflect/Field;Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 2
    .param p0, "sdkLimit"    # Ljava/lang/reflect/Field;
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 247
    iget-object v0, p1, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->thisObject:Ljava/lang/Object;

    const/4 v1, 0x2

    invoke-virtual {p0, v0, v1}, Ljava/lang/reflect/Field;->setInt(Ljava/lang/Object;I)V

    return-void
.end method

.method static synthetic lambda$beforeHookedMethod$0(Ljava/lang/reflect/Field;Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ljava/lang/reflect/Field;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/ThreadLocal;)V
    .locals 6
    .param p0, "sdkLabel"    # Ljava/lang/reflect/Field;
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;
    .param p2, "sdkLimit"    # Ljava/lang/reflect/Field;
    .param p3, "state"    # Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;
    .param p4, "scope"    # Ljava/lang/ThreadLocal;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 230
    iget-object v0, p1, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->thisObject:Ljava/lang/Object;

    invoke-virtual {p0, v0}, Ljava/lang/reflect/Field;->getInt(Ljava/lang/Object;)I

    move-result v0

    if-nez v0, :cond_6

    iget-object v0, p1, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->thisObject:Ljava/lang/Object;

    invoke-virtual {p2, v0}, Ljava/lang/reflect/Field;->getInt(Ljava/lang/Object;)I

    move-result v0

    const/4 v1, 0x2

    if-eq v0, v1, :cond_0

    goto :goto_2

    .line 231
    :cond_0
    invoke-virtual {p3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->sample()Ldev/gallerybridge/mediasync/Policy$Sample;

    move-result-object v0

    .line 232
    .local v0, "sample":Ldev/gallerybridge/mediasync/Policy$Sample;
    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal()I

    move-result v2

    if-ltz v2, :cond_5

    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal()I

    move-result v2

    const/4 v3, 0x3

    if-lt v2, v3, :cond_1

    goto :goto_1

    .line 233
    :cond_1
    invoke-static {p3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Ldev/gallerybridge/mediasync/Policy$Settings;

    move-result-object v2

    invoke-static {p3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetlatched(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Z

    move-result v4

    invoke-static {v2, v0, v4}, Ldev/gallerybridge/mediasync/Policy;->allowThermal(Ldev/gallerybridge/mediasync/Policy$Settings;Ldev/gallerybridge/mediasync/Policy$Sample;Z)Z

    move-result v2

    .line 234
    .local v2, "raised":Z
    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal()I

    move-result v4

    if-ne v4, v1, :cond_2

    if-nez v2, :cond_2

    return-void

    .line 235
    :cond_2
    new-instance v1, Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    invoke-virtual {p4}, Ljava/lang/ThreadLocal;->get()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    const/4 v5, 0x0

    invoke-direct {v1, v2, v4, v5}, Ldev/gallerybridge/mediasync/AgentHook$GateScope;-><init>(ZLdev/gallerybridge/mediasync/AgentHook$GateScope;Ldev/gallerybridge/mediasync/AgentHook-IA;)V

    .line 236
    .local v1, "frame":Ldev/gallerybridge/mediasync/AgentHook$GateScope;
    const-string v4, "gallerybridge.sdk.scope"

    invoke-virtual {p1, v4, v1}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->setObjectExtra(Ljava/lang/String;Ljava/lang/Object;)V

    .line 237
    invoke-virtual {p4, v1}, Ljava/lang/ThreadLocal;->set(Ljava/lang/Object;)V

    .line 238
    if-eqz v2, :cond_3

    iget-object v4, p1, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->thisObject:Ljava/lang/Object;

    invoke-virtual {p2, v4, v3}, Ljava/lang/reflect/Field;->setInt(Ljava/lang/Object;I)V

    .line 239
    :cond_3
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "observed="

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/Policy$Sample;->thermal()I

    move-result v4

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, "; before_limit=2; after_limit="

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    .line 240
    if-eqz v2, :cond_4

    const-string v4, "3"

    goto :goto_0

    :cond_4
    const-string v4, "2"

    :goto_0
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    .line 239
    const-string v4, "sdk_admission"

    invoke-virtual {p3, v4, v3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->event(Ljava/lang/String;Ljava/lang/String;)V

    .line 241
    return-void

    .line 232
    .end local v1    # "frame":Ldev/gallerybridge/mediasync/AgentHook$GateScope;
    .end local v2    # "raised":Z
    :cond_5
    :goto_1
    return-void

    .line 230
    .end local v0    # "sample":Ldev/gallerybridge/mediasync/Policy$Sample;
    :cond_6
    :goto_2
    return-void
.end method


# virtual methods
.method protected afterHookedMethod(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 4
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    .line 245
    const-string v0, "gallerybridge.sdk.scope"

    invoke-virtual {p1, v0}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->getObjectExtra(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    instance-of v1, v0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    if-eqz v1, :cond_3

    check-cast v0, Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    .line 247
    .local v0, "frame":Ldev/gallerybridge/mediasync/AgentHook$GateScope;
    :try_start_0
    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->raised()Z

    move-result v1

    if-eqz v1, :cond_0

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$sdkLimit:Ljava/lang/reflect/Field;

    new-instance v3, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda0;

    invoke-direct {v3, v2, p1}, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda0;-><init>(Ljava/lang/reflect/Field;Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V

    invoke-virtual {v1, v3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->safely(Ldev/gallerybridge/mediasync/AgentHook$Checked;)V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 249
    :cond_0
    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->prior()Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    move-result-object v1

    if-nez v1, :cond_1

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$scope:Ljava/lang/ThreadLocal;

    invoke-virtual {v1}, Ljava/lang/ThreadLocal;->remove()V

    goto :goto_0

    .line 250
    :cond_1
    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$scope:Ljava/lang/ThreadLocal;

    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->prior()Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/ThreadLocal;->set(Ljava/lang/Object;)V

    .line 251
    nop

    .line 252
    :goto_0
    return-void

    .line 249
    :catchall_0
    move-exception v1

    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->prior()Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    move-result-object v2

    if-nez v2, :cond_2

    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$scope:Ljava/lang/ThreadLocal;

    invoke-virtual {v2}, Ljava/lang/ThreadLocal;->remove()V

    goto :goto_1

    .line 250
    :cond_2
    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$scope:Ljava/lang/ThreadLocal;

    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/AgentHook$GateScope;->prior()Ldev/gallerybridge/mediasync/AgentHook$GateScope;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/ThreadLocal;->set(Ljava/lang/Object;)V

    .line 251
    :goto_1
    throw v1

    .line 245
    .end local v0    # "frame":Ldev/gallerybridge/mediasync/AgentHook$GateScope;
    :cond_3
    return-void
.end method

.method protected beforeHookedMethod(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 7
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    .line 228
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->thermalEnabled()Z

    move-result v0

    if-nez v0, :cond_0

    return-void

    .line 229
    :cond_0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$sdkLabel:Ljava/lang/reflect/Field;

    iget-object v4, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$sdkLimit:Ljava/lang/reflect/Field;

    iget-object v5, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v6, p0, Ldev/gallerybridge/mediasync/AgentHook$6;->val$scope:Ljava/lang/ThreadLocal;

    new-instance v1, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;

    move-object v3, p1

    .end local p1    # "p":Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;
    .local v3, "p":Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;
    invoke-direct/range {v1 .. v6}, Ldev/gallerybridge/mediasync/AgentHook$6$$ExternalSyntheticLambda1;-><init>(Ljava/lang/reflect/Field;Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ljava/lang/reflect/Field;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/ThreadLocal;)V

    invoke-virtual {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->safely(Ldev/gallerybridge/mediasync/AgentHook$Checked;)V

    .line 242
    return-void
.end method

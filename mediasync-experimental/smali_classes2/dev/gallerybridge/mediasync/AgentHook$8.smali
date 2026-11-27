.class Ldev/gallerybridge/mediasync/AgentHook$8;
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

.field final synthetic val$threshold:Ljava/lang/reflect/Field;


# direct methods
.method constructor <init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/reflect/Field;)V
    .locals 0

    .line 268
    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$8;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$8;->val$threshold:Ljava/lang/reflect/Field;

    invoke-direct {p0}, Lde/robv/android/xposed/XC_MethodHook;-><init>()V

    return-void
.end method

.method static synthetic lambda$beforeHookedMethod$0(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/reflect/Field;)V
    .locals 7
    .param p0, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;
    .param p1, "state"    # Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;
    .param p2, "threshold"    # Ljava/lang/reflect/Field;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 271
    iget-object v0, p0, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->args:[Ljava/lang/Object;

    const/4 v1, 0x0

    aget-object v0, v0, v1

    check-cast v0, Ljava/lang/Integer;

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v0

    .line 272
    .local v0, "real":I
    const/4 v2, 0x3

    const/4 v3, 0x1

    if-lt v0, v2, :cond_0

    invoke-static {p1, v3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fputlatched(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Z)V

    .line 273
    :cond_0
    iget-object v2, p0, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->thisObject:Ljava/lang/Object;

    invoke-virtual {p2, v2}, Ljava/lang/reflect/Field;->getInt(Ljava/lang/Object;)I

    move-result v2

    .line 274
    .local v2, "limit":I
    const/4 v4, 0x2

    if-ne v0, v4, :cond_1

    if-ne v2, v4, :cond_1

    iget-object v4, p0, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->thisObject:Ljava/lang/Object;

    .line 275
    invoke-virtual {p1, v4}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->monitored(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_1

    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->thermalEnabled()Z

    move-result v4

    if-eqz v4, :cond_1

    invoke-static {p1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Ldev/gallerybridge/mediasync/Policy$Settings;

    move-result-object v4

    .line 276
    invoke-virtual {p1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->sample()Ldev/gallerybridge/mediasync/Policy$Sample;

    move-result-object v5

    invoke-static {p1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetlatched(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Z

    move-result v6

    invoke-static {v4, v5, v6}, Ldev/gallerybridge/mediasync/Policy;->allowThermal(Ldev/gallerybridge/mediasync/Policy$Settings;Ldev/gallerybridge/mediasync/Policy$Sample;Z)Z

    move-result v4

    if-eqz v4, :cond_1

    move v4, v3

    goto :goto_0

    :cond_1
    move v4, v1

    .line 277
    .local v4, "overridden":Z
    :goto_0
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "observed="

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v5

    const-string v6, "; threshold="

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v5

    const-string v6, "; before_cancel="

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    if-lt v0, v2, :cond_2

    move v6, v3

    goto :goto_1

    :cond_2
    move v6, v1

    :goto_1
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v5

    const-string v6, "; after_cancel="

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    if-nez v4, :cond_3

    if-lt v0, v2, :cond_3

    move v1, v3

    :cond_3
    invoke-virtual {v5, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    const-string v3, "sdk_thermal_listener"

    invoke-virtual {p1, v3, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->event(Ljava/lang/String;Ljava/lang/String;)V

    .line 279
    if-eqz v4, :cond_4

    const/4 v1, 0x0

    invoke-virtual {p0, v1}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->setResult(Ljava/lang/Object;)V

    goto :goto_2

    .line 280
    :cond_4
    if-lt v0, v2, :cond_5

    iget-object v1, p0, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->thisObject:Ljava/lang/Object;

    invoke-virtual {p1, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->forget(Ljava/lang/Object;)V

    .line 281
    :cond_5
    :goto_2
    return-void
.end method


# virtual methods
.method protected beforeHookedMethod(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 4
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    .line 270
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$8;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$8;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$8;->val$threshold:Ljava/lang/reflect/Field;

    new-instance v3, Ldev/gallerybridge/mediasync/AgentHook$8$$ExternalSyntheticLambda0;

    invoke-direct {v3, p1, v1, v2}, Ldev/gallerybridge/mediasync/AgentHook$8$$ExternalSyntheticLambda0;-><init>(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/reflect/Field;)V

    invoke-virtual {v0, v3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->safely(Ldev/gallerybridge/mediasync/AgentHook$Checked;)V

    .line 282
    return-void
.end method

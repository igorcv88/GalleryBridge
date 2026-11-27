.class Ldev/gallerybridge/mediasync/AgentHook$11;
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

    .line 310
    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$11;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-direct {p0}, Lde/robv/android/xposed/XC_MethodHook;-><init>()V

    return-void
.end method

.method static synthetic lambda$beforeHookedMethod$0(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/time/Duration;)V
    .locals 3
    .param p0, "state"    # Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;
    .param p1, "delay"    # Ljava/time/Duration;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 313
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "millis="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {p1}, Ljava/time/Duration;->toMillis()J

    move-result-wide v1

    invoke-virtual {v0, v1, v2}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "scheduler_backoff"

    invoke-virtual {p0, v1, v0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->event(Ljava/lang/String;Ljava/lang/String;)V

    return-void
.end method


# virtual methods
.method protected beforeHookedMethod(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 4
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    .line 312
    iget-object v0, p1, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->args:[Ljava/lang/Object;

    const/4 v1, 0x1

    aget-object v0, v0, v1

    const-string v1, "JobService rescheduleDuration"

    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p1, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->args:[Ljava/lang/Object;

    const/4 v1, 0x2

    aget-object v0, v0, v1

    instance-of v1, v0, Ljava/time/Duration;

    if-eqz v1, :cond_0

    check-cast v0, Ljava/time/Duration;

    .line 313
    .local v0, "delay":Ljava/time/Duration;
    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$11;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$11;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    new-instance v3, Ldev/gallerybridge/mediasync/AgentHook$11$$ExternalSyntheticLambda0;

    invoke-direct {v3, v2, v0}, Ldev/gallerybridge/mediasync/AgentHook$11$$ExternalSyntheticLambda0;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/time/Duration;)V

    invoke-virtual {v1, v3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->safely(Ldev/gallerybridge/mediasync/AgentHook$Checked;)V

    .line 315
    .end local v0    # "delay":Ljava/time/Duration;
    :cond_0
    return-void
.end method

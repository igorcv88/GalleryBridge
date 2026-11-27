.class Ldev/gallerybridge/mediasync/AgentHook$12;
.super Lde/robv/android/xposed/XC_MethodHook;
.source "AgentHook.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Ldev/gallerybridge/mediasync/AgentHook;->jobHook(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Z)Lde/robv/android/xposed/XC_MethodHook;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

.field final synthetic val$stop:Z


# direct methods
.method constructor <init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Z)V
    .locals 0

    .line 327
    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$12;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iput-boolean p2, p0, Ldev/gallerybridge/mediasync/AgentHook$12;->val$stop:Z

    invoke-direct {p0}, Lde/robv/android/xposed/XC_MethodHook;-><init>()V

    return-void
.end method


# virtual methods
.method protected afterHookedMethod(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 6
    .param p1, "p"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    .line 329
    invoke-virtual {p1}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->hasThrowable()Z

    move-result v0

    if-eqz v0, :cond_0

    return-void

    .line 330
    :cond_0
    iget-object v0, p1, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->args:[Ljava/lang/Object;

    const/4 v1, 0x0

    aget-object v0, v0, v1

    check-cast v0, Landroid/app/job/JobParameters;

    .line 331
    .local v0, "job":Landroid/app/job/JobParameters;
    iget-object v1, p0, Ldev/gallerybridge/mediasync/AgentHook$12;->val$state:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iget-boolean v2, p0, Ldev/gallerybridge/mediasync/AgentHook$12;->val$stop:Z

    if-eqz v2, :cond_1

    const-string v2, "job_stop"

    goto :goto_0

    :cond_1
    const-string v2, "job_start"

    :goto_0
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "returned="

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    sget-object v4, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;

    invoke-virtual {p1}, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->getResult()Ljava/lang/Object;

    move-result-object v5

    invoke-virtual {v4, v5}, Ljava/lang/Boolean;->equals(Ljava/lang/Object;)Z

    move-result v4

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v3

    .line 332
    iget-boolean v4, p0, Ldev/gallerybridge/mediasync/AgentHook$12;->val$stop:Z

    if-eqz v4, :cond_2

    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "; stop_reason="

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v0}, Landroid/app/job/JobParameters;->getStopReason()I

    move-result v5

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    goto :goto_1

    :cond_2
    const-string v4, ""

    :goto_1
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    .line 331
    invoke-virtual {v1, v2, v3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->event(Ljava/lang/String;Ljava/lang/String;)V

    .line 333
    return-void
.end method

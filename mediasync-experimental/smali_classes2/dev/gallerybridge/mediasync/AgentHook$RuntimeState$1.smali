.class Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;
.super Ljava/lang/Object;
.source "AgentHook.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->start()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

.field final synthetic val$handler:Landroid/os/Handler;


# direct methods
.method public static synthetic $r8$lambda$0AS-6pL3dfceNhKRUW3R55V57HE(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;)V
    .locals 0

    invoke-direct {p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->lambda$run$1()V

    return-void
.end method

.method public static synthetic $r8$lambda$Hq59UfTjlfaz-G9FJ5ufkDUq_GU(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;)V
    .locals 0

    invoke-direct {p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->lambda$run$0()V

    return-void
.end method

.method constructor <init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Landroid/os/Handler;)V
    .locals 0
    .param p1, "this$0"    # Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x8010,
            0x1010
        }
        names = {
            null,
            null
        }
    .end annotation

    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()V"
        }
    .end annotation

    .line 379
    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->val$handler:Landroid/os/Handler;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private synthetic lambda$run$0()V
    .locals 12
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 382
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetpreferences(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Lde/robv/android/xposed/XSharedPreferences;

    move-result-object v0

    invoke-virtual {v0}, Lde/robv/android/xposed/XSharedPreferences;->reload()V

    .line 383
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetpreferences(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Lde/robv/android/xposed/XSharedPreferences;

    move-result-object v0

    const-string v1, "schema"

    const/4 v2, 0x0

    invoke-virtual {v0, v1, v2}, Lde/robv/android/xposed/XSharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    const/4 v1, 0x1

    if-eq v0, v1, :cond_0

    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$sfgetOFF()Ldev/gallerybridge/mediasync/Policy$Settings;

    move-result-object v2

    invoke-static {v0, v2}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fputsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ldev/gallerybridge/mediasync/Policy$Settings;)V

    goto :goto_0

    .line 385
    :cond_0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetpreferences(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Lde/robv/android/xposed/XSharedPreferences;

    move-result-object v0

    const-string v3, "profile"

    const/4 v4, -0x1

    invoke-virtual {v0, v3, v4}, Lde/robv/android/xposed/XSharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    .line 386
    .local v0, "profile":I
    iget-object v3, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    new-instance v5, Ldev/gallerybridge/mediasync/Policy$Settings;

    invoke-static {}, Ldev/gallerybridge/mediasync/Policy$Profile;->values()[Ldev/gallerybridge/mediasync/Policy$Profile;

    move-result-object v6

    aget-object v6, v6, v0

    iget-object v7, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v7}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetpreferences(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Lde/robv/android/xposed/XSharedPreferences;

    move-result-object v7

    .line 387
    const-string v8, "thermal"

    invoke-virtual {v7, v8, v2}, Lde/robv/android/xposed/XSharedPreferences;->getBoolean(Ljava/lang/String;Z)Z

    move-result v7

    iget-object v8, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v8}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetpreferences(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Lde/robv/android/xposed/XSharedPreferences;

    move-result-object v8

    const-string v9, "battery"

    invoke-virtual {v8, v9, v2}, Lde/robv/android/xposed/XSharedPreferences;->getBoolean(Ljava/lang/String;Z)Z

    move-result v8

    iget-object v9, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v9}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetpreferences(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Lde/robv/android/xposed/XSharedPreferences;

    move-result-object v9

    .line 388
    const-string v10, "limit"

    invoke-virtual {v9, v10, v4}, Lde/robv/android/xposed/XSharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v9

    iget-object v10, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v10}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetpreferences(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Lde/robv/android/xposed/XSharedPreferences;

    move-result-object v10

    const-string v11, "min"

    invoke-virtual {v10, v11, v4}, Lde/robv/android/xposed/XSharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v10

    iget-object v4, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v4}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetpreferences(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Lde/robv/android/xposed/XSharedPreferences;

    move-result-object v4

    .line 389
    const-string v11, "diag"

    invoke-virtual {v4, v11, v2}, Lde/robv/android/xposed/XSharedPreferences;->getBoolean(Ljava/lang/String;Z)Z

    move-result v11

    invoke-direct/range {v5 .. v11}, Ldev/gallerybridge/mediasync/Policy$Settings;-><init>(Ldev/gallerybridge/mediasync/Policy$Profile;ZZIIZ)V

    invoke-static {v3, v5}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fputsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ldev/gallerybridge/mediasync/Policy$Settings;)V

    .line 391
    .end local v0    # "profile":I
    :goto_0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetpower(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Landroid/os/PowerManager;

    move-result-object v0

    invoke-virtual {v0}, Landroid/os/PowerManager;->getCurrentThermalStatus()I

    move-result v0

    const/4 v2, 0x3

    if-lt v0, v2, :cond_1

    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fputlatched(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Z)V

    .line 392
    :cond_1
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetsettings(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Ldev/gallerybridge/mediasync/Policy$Settings;

    move-result-object v0

    invoke-virtual {v0}, Ldev/gallerybridge/mediasync/Policy$Settings;->profile()Ldev/gallerybridge/mediasync/Policy$Profile;

    move-result-object v0

    sget-object v1, Ldev/gallerybridge/mediasync/Policy$Profile;->OFF:Ldev/gallerybridge/mediasync/Policy$Profile;

    const/4 v2, 0x0

    if-eq v0, v1, :cond_2

    .line 393
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    new-instance v1, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;

    iget-object v3, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fgetpower(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)Landroid/os/PowerManager;

    move-result-object v3

    const/16 v4, 0xa

    invoke-virtual {v3, v4}, Landroid/os/PowerManager;->getThermalHeadroom(I)F

    move-result v3

    invoke-static {}, Landroid/os/SystemClock;->elapsedRealtime()J

    move-result-wide v4

    invoke-direct {v1, v3, v4, v5, v2}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;-><init>(FJLdev/gallerybridge/mediasync/AgentHook-IA;)V

    invoke-static {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fputprediction(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;)V

    goto :goto_1

    .line 394
    :cond_2
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    new-instance v1, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;

    const/high16 v3, 0x7fc00000    # Float.NaN

    const-wide/16 v4, 0x0

    invoke-direct {v1, v3, v4, v5, v2}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;-><init>(FJLdev/gallerybridge/mediasync/AgentHook-IA;)V

    invoke-static {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fputprediction(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$Forecast;)V

    .line 395
    :goto_1
    return-void
.end method

.method private synthetic lambda$run$1()V
    .locals 1
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 396
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    invoke-static {v0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$mrevokeIfNeeded(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 3

    .line 381
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    new-instance v1, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1$$ExternalSyntheticLambda0;

    invoke-direct {v1, p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1$$ExternalSyntheticLambda0;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;)V

    invoke-virtual {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->safely(Ldev/gallerybridge/mediasync/AgentHook$Checked;)V

    .line 396
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    new-instance v1, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1$$ExternalSyntheticLambda1;

    invoke-direct {v1, p0}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1$$ExternalSyntheticLambda1;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;)V

    invoke-virtual {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->safely(Ldev/gallerybridge/mediasync/AgentHook$Checked;)V

    .line 397
    iget-object v0, p0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState$1;->val$handler:Landroid/os/Handler;

    const-wide/16 v1, 0x7d0

    invoke-virtual {v0, p0, v1, v2}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 398
    return-void
.end method

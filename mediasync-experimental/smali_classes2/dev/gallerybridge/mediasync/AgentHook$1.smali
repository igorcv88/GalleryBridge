.class Ldev/gallerybridge/mediasync/AgentHook$1;
.super Lde/robv/android/xposed/XC_MethodHook;
.source "AgentHook.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Ldev/gallerybridge/mediasync/AgentHook;->handleLoadPackage(Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field private attempted:Z

.field final synthetic this$0:Ldev/gallerybridge/mediasync/AgentHook;

.field final synthetic val$target:Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;


# direct methods
.method constructor <init>(Ldev/gallerybridge/mediasync/AgentHook;Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;)V
    .locals 0
    .param p1, "this$0"    # Ldev/gallerybridge/mediasync/AgentHook;
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

    .line 50
    iput-object p1, p0, Ldev/gallerybridge/mediasync/AgentHook$1;->this$0:Ldev/gallerybridge/mediasync/AgentHook;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/AgentHook$1;->val$target:Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;

    invoke-direct {p0}, Lde/robv/android/xposed/XC_MethodHook;-><init>()V

    return-void
.end method


# virtual methods
.method protected afterHookedMethod(Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;)V
    .locals 5
    .param p1, "param"    # Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;

    .line 54
    const-string v0, "GB-MediaSync"

    iget-object v1, p1, Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;->args:[Ljava/lang/Object;

    const/4 v2, 0x0

    aget-object v1, v1, v2

    check-cast v1, Landroid/content/Context;

    .line 55
    .local v1, "context":Landroid/content/Context;
    iget-boolean v2, p0, Ldev/gallerybridge/mediasync/AgentHook$1;->attempted:Z

    if-nez v2, :cond_2

    const-string v2, "com.samsung.android.agent.storage"

    invoke-virtual {v1}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-nez v2, :cond_0

    goto :goto_1

    .line 56
    :cond_0
    const/4 v2, 0x1

    iput-boolean v2, p0, Ldev/gallerybridge/mediasync/AgentHook$1;->attempted:Z

    .line 58
    :try_start_0
    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$1;->val$target:Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;

    iget-object v2, v2, Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;->appInfo:Landroid/content/pm/ApplicationInfo;

    iget-object v2, v2, Landroid/content/pm/ApplicationInfo;->sourceDir:Ljava/lang/String;

    iget-object v3, p0, Ldev/gallerybridge/mediasync/AgentHook$1;->val$target:Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;

    iget-object v3, v3, Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;->appInfo:Landroid/content/pm/ApplicationInfo;

    iget-object v3, v3, Landroid/content/pm/ApplicationInfo;->splitSourceDirs:[Ljava/lang/String;

    invoke-static {v1, v2, v3}, Ldev/gallerybridge/mediasync/AgentHook;->-$$Nest$smmatches(Landroid/content/Context;Ljava/lang/String;[Ljava/lang/String;)Z

    move-result v2

    if-nez v2, :cond_1

    .line 59
    const-string v2, "profile=unsupported; hooks=0"

    invoke-static {v0, v2}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 60
    return-void

    .line 62
    :cond_1
    iget-object v2, p0, Ldev/gallerybridge/mediasync/AgentHook$1;->val$target:Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;

    iget-object v2, v2, Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;->classLoader:Ljava/lang/ClassLoader;

    invoke-static {v1, v2}, Ldev/gallerybridge/mediasync/AgentHook;->-$$Nest$sminstall(Landroid/content/Context;Ljava/lang/ClassLoader;)V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 65
    goto :goto_0

    .line 63
    :catchall_0
    move-exception v2

    .line 64
    .local v2, "error":Ljava/lang/Throwable;
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "profile=unavailable; cause="

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v2}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v0, v3}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    .line 66
    .end local v2    # "error":Ljava/lang/Throwable;
    :goto_0
    return-void

    .line 55
    :cond_2
    :goto_1
    return-void
.end method

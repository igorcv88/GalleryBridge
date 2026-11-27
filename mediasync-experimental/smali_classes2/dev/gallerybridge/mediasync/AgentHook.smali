.class public final Ldev/gallerybridge/mediasync/AgentHook;
.super Ljava/lang/Object;
.source "AgentHook.java"

# interfaces
.implements Lde/robv/android/xposed/IXposedHookLoadPackage;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;,
        Ldev/gallerybridge/mediasync/AgentHook$Checked;,
        Ldev/gallerybridge/mediasync/AgentHook$GateScope;
    }
.end annotation


# static fields
.field private static final AGENT:Ljava/lang/String; = "com.samsung.android.agent.storage"

.field private static final DIGESTS:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map<",
            "Ljava/lang/String;",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field private static final FIND:Ljava/lang/String; = "com.samsung.android.agent.storage.domain.usecase.precondition.FindUnmetDeviceConditionUseCase"

.field private static final INTERNAL:Ljava/lang/String; = "com.google.android.gms.internal.media_sync."

.field private static final MODULE:Ljava/lang/String; = "dev.gallerybridge.mediasync"

.field private static final SERVICE:Ljava/lang/String; = "com.google.android.libraries.mediasync.impl.scheduler.UnifiedRemoteSyncJobService"

.field private static final TAG:Ljava/lang/String; = "GB-MediaSync"

.field private static final VERSION_CODE:J = 0x99723c2L

.field private static final WORKER:Ljava/lang/String; = "com.samsung.android.agent.storage.work.ImportWorker"


# direct methods
.method static bridge synthetic -$$Nest$smcaller(Ljava/lang/String;Ljava/lang/String;)Z
    .locals 0

    invoke-static {p0, p1}, Ldev/gallerybridge/mediasync/AgentHook;->caller(Ljava/lang/String;Ljava/lang/String;)Z

    move-result p0

    return p0
.end method

.method static bridge synthetic -$$Nest$smenumName(Ljava/lang/Object;)Ljava/lang/String;
    .locals 0

    invoke-static {p0}, Ldev/gallerybridge/mediasync/AgentHook;->enumName(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method static bridge synthetic -$$Nest$sminstall(Landroid/content/Context;Ljava/lang/ClassLoader;)V
    .locals 0

    invoke-static {p0, p1}, Ldev/gallerybridge/mediasync/AgentHook;->install(Landroid/content/Context;Ljava/lang/ClassLoader;)V

    return-void
.end method

.method static bridge synthetic -$$Nest$smmatches(Landroid/content/Context;Ljava/lang/String;[Ljava/lang/String;)Z
    .locals 0

    invoke-static {p0, p1, p2}, Ldev/gallerybridge/mediasync/AgentHook;->matches(Landroid/content/Context;Ljava/lang/String;[Ljava/lang/String;)Z

    move-result p0

    return p0
.end method

.method static constructor <clinit>()V
    .locals 6

    .line 41
    const-string v4, "classes2.dex"

    const-string v5, "b9d9684359dc5fbc1a559833e7c0490a7a1223ac4894986d24139019a2494391"

    const-string v0, "AndroidManifest.xml"

    const-string v1, "66b8c5c210e73c3f953daab2ae5de295c9b6ee2267f6260649ed79645710e7f1"

    const-string v2, "classes.dex"

    const-string v3, "8b94c295de2cd70e998276225f012d86219583f11ac0bf746b8cb893594b9504"

    invoke-static/range {v0 .. v5}, Ljava/util/Map;->of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;

    move-result-object v0

    sput-object v0, Ldev/gallerybridge/mediasync/AgentHook;->DIGESTS:Ljava/util/Map;

    return-void
.end method

.method public constructor <init>()V
    .locals 0

    .line 31
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static caller(Ljava/lang/String;Ljava/lang/String;)Z
    .locals 6
    .param p0, "owner"    # Ljava/lang/String;
    .param p1, "method"    # Ljava/lang/String;

    .line 338
    invoke-static {}, Ljava/lang/Thread;->currentThread()Ljava/lang/Thread;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Thread;->getStackTrace()[Ljava/lang/StackTraceElement;

    move-result-object v0

    array-length v1, v0

    const/4 v2, 0x0

    move v3, v2

    :goto_0
    if-ge v3, v1, :cond_1

    aget-object v4, v0, v3

    .line 339
    .local v4, "frame":Ljava/lang/StackTraceElement;
    invoke-virtual {v4}, Ljava/lang/StackTraceElement;->getClassName()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {p0, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v5

    if-eqz v5, :cond_0

    invoke-virtual {v4}, Ljava/lang/StackTraceElement;->getMethodName()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {p1, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v5

    if-eqz v5, :cond_0

    const/4 v0, 0x1

    return v0

    .line 338
    .end local v4    # "frame":Ljava/lang/StackTraceElement;
    :cond_0
    add-int/lit8 v3, v3, 0x1

    goto :goto_0

    .line 341
    :cond_1
    return v2
.end method

.method private static dexNamesMatch(Ljava/util/zip/ZipFile;I)Z
    .locals 5
    .param p0, "zip"    # Ljava/util/zip/ZipFile;
    .param p1, "expected"    # I

    .line 107
    const/4 v0, 0x0

    .line 108
    .local v0, "found":I
    invoke-virtual {p0}, Ljava/util/zip/ZipFile;->entries()Ljava/util/Enumeration;

    move-result-object v1

    .line 109
    .local v1, "list":Ljava/util/Enumeration;, "Ljava/util/Enumeration<+Ljava/util/zip/ZipEntry;>;"
    :goto_0
    invoke-interface {v1}, Ljava/util/Enumeration;->hasMoreElements()Z

    move-result v2

    const/4 v3, 0x0

    if-eqz v2, :cond_2

    .line 110
    invoke-interface {v1}, Ljava/util/Enumeration;->nextElement()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/util/zip/ZipEntry;

    invoke-virtual {v2}, Ljava/util/zip/ZipEntry;->getName()Ljava/lang/String;

    move-result-object v2

    .line 111
    .local v2, "name":Ljava/lang/String;
    const-string v4, "classes[0-9]*\\.dex"

    invoke-virtual {v2, v4}, Ljava/lang/String;->matches(Ljava/lang/String;)Z

    move-result v4

    if-nez v4, :cond_0

    goto :goto_0

    .line 112
    :cond_0
    sget-object v4, Ldev/gallerybridge/mediasync/AgentHook;->DIGESTS:Ljava/util/Map;

    invoke-interface {v4, v2}, Ljava/util/Map;->containsKey(Ljava/lang/Object;)Z

    move-result v4

    if-nez v4, :cond_1

    return v3

    .line 113
    :cond_1
    nop

    .end local v2    # "name":Ljava/lang/String;
    add-int/lit8 v0, v0, 0x1

    .line 114
    goto :goto_0

    .line 115
    :cond_2
    if-ne v0, p1, :cond_3

    const/4 v3, 0x1

    :cond_3
    return v3
.end method

.method private static enumName(Ljava/lang/Object;)Ljava/lang/String;
    .locals 2
    .param p0, "value"    # Ljava/lang/Object;

    .line 345
    instance-of v0, p0, Ljava/lang/Enum;

    if-eqz v0, :cond_0

    move-object v0, p0

    check-cast v0, Ljava/lang/Enum;

    .local v0, "item":Ljava/lang/Enum;, "Ljava/lang/Enum<*>;"
    invoke-virtual {v0}, Ljava/lang/Enum;->name()Ljava/lang/String;

    move-result-object v1

    goto :goto_0

    .end local v0    # "item":Ljava/lang/Enum;, "Ljava/lang/Enum<*>;"
    :cond_0
    const-string v1, "unknown"

    :goto_0
    return-object v1
.end method

.method private static install(Landroid/content/Context;Ljava/lang/ClassLoader;)V
    .locals 35
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "loader"    # Ljava/lang/ClassLoader;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 130
    move-object/from16 v0, p1

    const-string v1, "com.samsung.android.agent.storage.domain.model.ThermalStatus"

    invoke-static {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v1

    .line 131
    .local v1, "thermalStatus":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const-string v2, "gd.e"

    invoke-static {v0, v2}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v2

    .line 132
    .local v2, "continuation":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const-string v3, "com.samsung.android.agent.storage.domain.policy.ThermalSyncPolicy"

    invoke-static {v0, v3}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v3

    .line 133
    .local v3, "policy":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const-string v4, "com.samsung.android.agent.storage.data.repository.DeviceConditionRepositoryImpl"

    invoke-static {v0, v4}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v4

    .line 134
    .local v4, "repository":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const-string v5, "com.google.android.libraries.mediasync.impl.scheduler.UnifiedRemoteSyncJobService"

    invoke-static {v0, v5}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v5

    .line 135
    .local v5, "registry":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    sget-object v6, Ljava/lang/Boolean;->TYPE:Ljava/lang/Class;

    const/4 v7, 0x1

    new-array v8, v7, [Ljava/lang/Class;

    const/4 v9, 0x0

    aput-object v1, v8, v9

    const-string v10, "isBlocked"

    invoke-static {v3, v10, v6, v8}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v12

    .line 136
    .local v12, "thermal":Ljava/lang/reflect/Method;
    sget-object v6, Ljava/lang/Boolean;->TYPE:Ljava/lang/Class;

    new-array v8, v9, [Ljava/lang/Class;

    const-string v10, "isBatteryLow"

    invoke-static {v4, v10, v6, v8}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v13

    .line 137
    .local v13, "battery":Ljava/lang/reflect/Method;
    const-string v6, "com.samsung.android.agent.storage.domain.usecase.precondition.FindUnmetDeviceConditionUseCase"

    invoke-static {v0, v6}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v6

    const-class v8, Ljava/lang/Object;

    new-array v10, v7, [Ljava/lang/Class;

    aput-object v2, v10, v9

    const-string v11, "invoke"

    invoke-static {v6, v11, v8, v10}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v14

    .line 138
    .local v14, "unmet":Ljava/lang/reflect/Method;
    const-string v6, "com.google.android.gms.internal.media_sync.zzzo"

    invoke-static {v0, v6}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v6

    const-class v8, Ljava/lang/Object;

    const/4 v10, 0x3

    new-array v11, v10, [Ljava/lang/Class;

    sget-object v15, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;

    aput-object v15, v11, v9

    .line 139
    const-string v15, "qd.b"

    invoke-static {v0, v15}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v15

    aput-object v15, v11, v7

    const/4 v15, 0x2

    aput-object v2, v11, v15

    .line 138
    move/from16 v16, v10

    const-string v10, "zzc"

    invoke-static {v6, v10, v8, v11}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v6

    .line 140
    .local v6, "sdkGuard":Ljava/lang/reflect/Method;
    const-string v8, "com.google.android.gms.internal.media_sync.zzzn"

    invoke-static {v0, v8}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v8

    .line 141
    .local v8, "gate":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const-class v11, Ljava/lang/Object;

    move/from16 v17, v9

    new-array v9, v7, [Ljava/lang/Class;

    const-class v18, Ljava/lang/Object;

    aput-object v18, v9, v17

    move/from16 v18, v7

    const-string v7, "invokeSuspend"

    invoke-static {v8, v7, v11, v9}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v26

    .line 142
    .local v26, "sdkCycle":Ljava/lang/reflect/Method;
    const-string v7, "zza"

    invoke-static {v8, v7}, Ldev/gallerybridge/mediasync/AgentHook;->integerField(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;

    move-result-object v27

    .line 143
    .local v27, "sdkLabel":Ljava/lang/reflect/Field;
    invoke-static {v8, v10}, Ldev/gallerybridge/mediasync/AgentHook;->integerField(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;

    move-result-object v28

    .line 144
    .local v28, "sdkLimit":Ljava/lang/reflect/Field;
    const-string v9, "com.google.android.gms.internal.media_sync.zzzl"

    invoke-static {v0, v9}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v9

    .line 145
    .local v9, "listener":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    new-array v11, v15, [Ljava/lang/Class;

    sget-object v19, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;

    aput-object v19, v11, v17

    move/from16 v19, v15

    const-string v15, "mg.j0"

    invoke-static {v0, v15}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v15

    aput-object v15, v11, v18

    invoke-virtual {v9, v11}, Ljava/lang/Class;->getDeclaredConstructor([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;

    move-result-object v29

    .line 146
    .local v29, "listenerConstructor":Ljava/lang/reflect/Constructor;, "Ljava/lang/reflect/Constructor<*>;"
    sget-object v11, Ljava/lang/Void;->TYPE:Ljava/lang/Class;

    move-object/from16 v30, v1

    move/from16 v15, v18

    .end local v1    # "thermalStatus":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .local v30, "thermalStatus":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    new-array v1, v15, [Ljava/lang/Class;

    sget-object v15, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;

    aput-object v15, v1, v17

    const-string v15, "onThermalStatusChanged"

    invoke-static {v9, v15, v11, v1}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v1

    .line 147
    .local v1, "sdkListener":Ljava/lang/reflect/Method;
    invoke-virtual {v1}, Ljava/lang/reflect/Method;->getDeclaringClass()Ljava/lang/Class;

    move-result-object v11

    invoke-virtual {v11, v7}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    move-result-object v11

    .line 148
    .local v11, "threshold":Ljava/lang/reflect/Field;
    invoke-virtual {v11}, Ljava/lang/reflect/Field;->getType()Ljava/lang/Class;

    move-result-object v15

    move-object/from16 v20, v1

    .end local v1    # "sdkListener":Ljava/lang/reflect/Method;
    .local v20, "sdkListener":Ljava/lang/reflect/Method;
    sget-object v1, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;

    if-ne v15, v1, :cond_0

    .line 149
    const/4 v15, 0x1

    invoke-virtual {v11, v15}, Ljava/lang/reflect/Field;->setAccessible(Z)V

    .line 150
    sget-object v1, Ljava/lang/Boolean;->TYPE:Ljava/lang/Class;

    move-object/from16 v31, v2

    .end local v2    # "continuation":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .local v31, "continuation":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    new-array v2, v15, [Ljava/lang/Class;

    const-class v18, Landroid/app/job/JobParameters;

    aput-object v18, v2, v17

    const-string v15, "onStartJob"

    invoke-static {v5, v15, v1, v2}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v1

    .line 151
    .local v1, "start":Ljava/lang/reflect/Method;
    sget-object v2, Ljava/lang/Boolean;->TYPE:Ljava/lang/Class;

    move-object/from16 v21, v1

    const/4 v15, 0x1

    .end local v1    # "start":Ljava/lang/reflect/Method;
    .local v21, "start":Ljava/lang/reflect/Method;
    new-array v1, v15, [Ljava/lang/Class;

    const-class v15, Landroid/app/job/JobParameters;

    aput-object v15, v1, v17

    const-string v15, "onStopJob"

    invoke-static {v5, v15, v2, v1}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v1

    .line 152
    .local v1, "stop":Ljava/lang/reflect/Method;
    const-class v2, Ljava/lang/Object;

    const/4 v15, 0x6

    new-array v15, v15, [Ljava/lang/Class;

    const-class v22, Ljava/lang/Exception;

    aput-object v22, v15, v17

    const-class v22, Landroid/app/job/JobParameters;

    const/16 v18, 0x1

    aput-object v22, v15, v18

    sget-object v22, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;

    aput-object v22, v15, v19

    sget-object v22, Ljava/lang/Long;->TYPE:Ljava/lang/Class;

    aput-object v22, v15, v16

    .line 153
    move-object/from16 v22, v1

    .end local v1    # "stop":Ljava/lang/reflect/Method;
    .local v22, "stop":Ljava/lang/reflect/Method;
    const-string v1, "com.google.android.gms.internal.media_sync.zzanl"

    invoke-static {v0, v1}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v1

    move-object/from16 v23, v1

    const/4 v1, 0x4

    aput-object v23, v15, v1

    const/16 v23, 0x5

    aput-object v31, v15, v23

    .line 152
    const-string v1, "zzt"

    invoke-static {v5, v1, v2, v15}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v1

    .line 154
    .local v1, "failure":Ljava/lang/reflect/Method;
    const-string v2, "com.google.android.gms.internal.media_sync.zzddj"

    invoke-static {v0, v2}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v2

    .line 155
    .local v2, "rpc":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const-string v15, "com.google.android.gms.internal.media_sync.zzddg"

    invoke-static {v0, v15}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v15

    .line 156
    .local v15, "rpcStatus":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    move-object/from16 v24, v1

    .end local v1    # "failure":Ljava/lang/reflect/Method;
    .local v24, "failure":Ljava/lang/reflect/Method;
    const-string v1, "zzb"

    move-object/from16 v32, v3

    move-object/from16 v33, v4

    move/from16 v3, v17

    .end local v3    # "policy":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v4    # "repository":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .local v32, "policy":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .local v33, "repository":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    new-array v4, v3, [Ljava/lang/Class;

    invoke-static {v2, v1, v15, v4}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v1

    .line 157
    .local v1, "status":Ljava/lang/reflect/Method;
    const-string v4, "com.google.android.gms.internal.media_sync.zzddc"

    invoke-static {v0, v4}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v4

    move-object/from16 v17, v1

    .end local v1    # "status":Ljava/lang/reflect/Method;
    .local v17, "status":Ljava/lang/reflect/Method;
    new-array v1, v3, [Ljava/lang/Class;

    invoke-static {v15, v7, v4, v1}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v1

    .line 158
    .local v1, "code":Ljava/lang/reflect/Method;
    const-string v4, "com.google.android.gms.internal.media_sync.zzaqp"

    invoke-static {v0, v4}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v4

    const-class v7, Ljava/lang/Object;

    move-object/from16 v34, v1

    move/from16 v25, v3

    move/from16 v3, v19

    .end local v1    # "code":Ljava/lang/reflect/Method;
    .local v34, "code":Ljava/lang/reflect/Method;
    new-array v1, v3, [Ljava/lang/Class;

    .line 159
    const-string v3, "com.google.android.libraries.mediasync.MediaSyncSummary$PausedReason"

    invoke-static {v0, v3}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v3

    aput-object v3, v1, v25

    const/16 v18, 0x1

    aput-object v31, v1, v18

    .line 158
    invoke-static {v4, v10, v7, v1}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v1

    .line 160
    .local v1, "schedulerState":Ljava/lang/reflect/Method;
    const-string v3, "com.google.android.libraries.mediasync.impl.scheduler.zzbi"

    invoke-static {v0, v3}, Ldev/gallerybridge/mediasync/AgentHook;->type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v3

    sget-object v4, Ljava/lang/Void;->TYPE:Ljava/lang/Class;

    const/4 v7, 0x4

    new-array v7, v7, [Ljava/lang/Class;

    const-class v23, Ljava/util/Set;

    aput-object v23, v7, v25

    const-class v23, Ljava/lang/String;

    aput-object v23, v7, v18

    const-class v18, Ljava/time/Duration;

    const/16 v19, 0x2

    aput-object v18, v7, v19

    sget-object v18, Ljava/lang/Boolean;->TYPE:Ljava/lang/Class;

    aput-object v18, v7, v16

    invoke-static {v3, v10, v4, v7}, Ldev/gallerybridge/mediasync/AgentHook;->method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v25

    .line 162
    .local v25, "backoff":Ljava/lang/reflect/Method;
    move-object/from16 v16, v20

    move-object/from16 v18, v21

    move-object/from16 v19, v22

    move-object/from16 v20, v24

    move-object/from16 v23, v34

    move-object/from16 v24, v1

    move-object/from16 v21, v2

    move-object v1, v15

    move-object/from16 v22, v17

    move-object v15, v6

    move-object/from16 v17, v11

    move-object/from16 v11, p0

    .end local v2    # "rpc":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v6    # "sdkGuard":Ljava/lang/reflect/Method;
    .end local v11    # "threshold":Ljava/lang/reflect/Field;
    .end local v34    # "code":Ljava/lang/reflect/Method;
    .local v1, "rpcStatus":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .local v15, "sdkGuard":Ljava/lang/reflect/Method;
    .local v16, "sdkListener":Ljava/lang/reflect/Method;
    .local v17, "threshold":Ljava/lang/reflect/Field;
    .local v18, "start":Ljava/lang/reflect/Method;
    .local v19, "stop":Ljava/lang/reflect/Method;
    .local v20, "failure":Ljava/lang/reflect/Method;
    .local v21, "rpc":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .local v22, "status":Ljava/lang/reflect/Method;
    .local v23, "code":Ljava/lang/reflect/Method;
    .local v24, "schedulerState":Ljava/lang/reflect/Method;
    invoke-static/range {v11 .. v29}, Ldev/gallerybridge/mediasync/AgentHook;->installHooks(Landroid/content/Context;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Field;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/Class;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;Ljava/lang/reflect/Constructor;)V

    .line 164
    .end local v23    # "code":Ljava/lang/reflect/Method;
    .restart local v34    # "code":Ljava/lang/reflect/Method;
    return-void

    .line 148
    .end local v1    # "rpcStatus":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v15    # "sdkGuard":Ljava/lang/reflect/Method;
    .end local v16    # "sdkListener":Ljava/lang/reflect/Method;
    .end local v17    # "threshold":Ljava/lang/reflect/Field;
    .end local v18    # "start":Ljava/lang/reflect/Method;
    .end local v19    # "stop":Ljava/lang/reflect/Method;
    .end local v21    # "rpc":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v22    # "status":Ljava/lang/reflect/Method;
    .end local v24    # "schedulerState":Ljava/lang/reflect/Method;
    .end local v25    # "backoff":Ljava/lang/reflect/Method;
    .end local v31    # "continuation":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v32    # "policy":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v33    # "repository":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v34    # "code":Ljava/lang/reflect/Method;
    .local v2, "continuation":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .restart local v3    # "policy":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .restart local v4    # "repository":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .restart local v6    # "sdkGuard":Ljava/lang/reflect/Method;
    .restart local v11    # "threshold":Ljava/lang/reflect/Field;
    .local v20, "sdkListener":Ljava/lang/reflect/Method;
    :cond_0
    move-object/from16 v31, v2

    .end local v2    # "continuation":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .restart local v31    # "continuation":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    new-instance v1, Ljava/lang/NoSuchFieldException;

    const-string v2, "SDK threshold"

    invoke-direct {v1, v2}, Ljava/lang/NoSuchFieldException;-><init>(Ljava/lang/String;)V

    throw v1
.end method

.method private static installHooks(Landroid/content/Context;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Field;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/Class;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;Ljava/lang/reflect/Constructor;)V
    .locals 17
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "thermal"    # Ljava/lang/reflect/Method;
    .param p2, "battery"    # Ljava/lang/reflect/Method;
    .param p3, "unmet"    # Ljava/lang/reflect/Method;
    .param p4, "sdkGuard"    # Ljava/lang/reflect/Method;
    .param p5, "sdkListener"    # Ljava/lang/reflect/Method;
    .param p6, "threshold"    # Ljava/lang/reflect/Field;
    .param p7, "start"    # Ljava/lang/reflect/Method;
    .param p8, "stop"    # Ljava/lang/reflect/Method;
    .param p9, "failure"    # Ljava/lang/reflect/Method;
    .param p11, "status"    # Ljava/lang/reflect/Method;
    .param p12, "code"    # Ljava/lang/reflect/Method;
    .param p13, "schedulerState"    # Ljava/lang/reflect/Method;
    .param p14, "backoff"    # Ljava/lang/reflect/Method;
    .param p15, "sdkCycle"    # Ljava/lang/reflect/Method;
    .param p16, "sdkLabel"    # Ljava/lang/reflect/Field;
    .param p17, "sdkLimit"    # Ljava/lang/reflect/Field;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/reflect/Field;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/Class<",
            "*>;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/reflect/Method;",
            "Ljava/lang/reflect/Field;",
            "Ljava/lang/reflect/Field;",
            "Ljava/lang/reflect/Constructor<",
            "*>;)V"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 178
    .local p10, "rpc":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .local p18, "listenerConstructor":Ljava/lang/reflect/Constructor;, "Ljava/lang/reflect/Constructor<*>;"
    move-object/from16 v1, p5

    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;

    move-object/from16 v2, p0

    invoke-direct {v0, v2, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;-><init>(Landroid/content/Context;Ljava/lang/reflect/Method;)V

    move-object v3, v0

    .line 179
    .local v3, "state":Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;
    new-instance v0, Ljava/lang/ThreadLocal;

    invoke-direct {v0}, Ljava/lang/ThreadLocal;-><init>()V

    move-object v4, v0

    .line 180
    .local v4, "scope":Ljava/lang/ThreadLocal;, "Ljava/lang/ThreadLocal<Ldev/gallerybridge/mediasync/AgentHook$GateScope;>;"
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    move-object v5, v0

    .line 182
    .local v5, "hooks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lde/robv/android/xposed/XC_MethodHook$Unhook;>;"
    :try_start_0
    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$2;

    invoke-direct {v0, v3}, Ldev/gallerybridge/mediasync/AgentHook$2;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_a

    move-object/from16 v7, p1

    :try_start_1
    invoke-static {v7, v0}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    move-result-object v0

    invoke-virtual {v5, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 196
    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$3;

    invoke-direct {v0, v3}, Ldev/gallerybridge/mediasync/AgentHook$3;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_9

    move-object/from16 v8, p2

    :try_start_2
    invoke-static {v8, v0}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    move-result-object v0

    invoke-virtual {v5, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 211
    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$4;

    invoke-direct {v0, v3}, Ldev/gallerybridge/mediasync/AgentHook$4;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_8

    move-object/from16 v9, p3

    :try_start_3
    invoke-static {v9, v0}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    move-result-object v0

    invoke-virtual {v5, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 219
    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$5;

    invoke-direct {v0, v3}, Ldev/gallerybridge/mediasync/AgentHook$5;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_7

    move-object/from16 v10, p4

    :try_start_4
    invoke-static {v10, v0}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    move-result-object v0

    invoke-virtual {v5, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 224
    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$6;
    :try_end_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_6

    move-object/from16 v11, p16

    move-object/from16 v12, p17

    :try_start_5
    invoke-direct {v0, v3, v11, v12, v4}, Ldev/gallerybridge/mediasync/AgentHook$6;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;Ljava/lang/ThreadLocal;)V
    :try_end_5
    .catchall {:try_start_5 .. :try_end_5} :catchall_5

    move-object/from16 v13, p15

    :try_start_6
    invoke-static {v13, v0}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    move-result-object v0

    invoke-virtual {v5, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 254
    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$7;

    invoke-direct {v0, v4, v3}, Ldev/gallerybridge/mediasync/AgentHook$7;-><init>(Ljava/lang/ThreadLocal;Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V
    :try_end_6
    .catchall {:try_start_6 .. :try_end_6} :catchall_4

    move-object/from16 v14, p18

    :try_start_7
    invoke-static {v14, v0}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    move-result-object v0

    invoke-virtual {v5, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 268
    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$8;
    :try_end_7
    .catchall {:try_start_7 .. :try_end_7} :catchall_3

    move-object/from16 v15, p6

    :try_start_8
    invoke-direct {v0, v3, v15}, Ldev/gallerybridge/mediasync/AgentHook$8;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/reflect/Field;)V

    invoke-static {v1, v0}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    move-result-object v0

    invoke-virtual {v5, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 284
    const/4 v0, 0x0

    invoke-static {v3, v0}, Ldev/gallerybridge/mediasync/AgentHook;->jobHook(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Z)Lde/robv/android/xposed/XC_MethodHook;

    move-result-object v0
    :try_end_8
    .catchall {:try_start_8 .. :try_end_8} :catchall_2

    move-object/from16 v6, p7

    :try_start_9
    invoke-static {v6, v0}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    move-result-object v0

    invoke-virtual {v5, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 285
    const/4 v1, 0x1

    invoke-static {v3, v1}, Ldev/gallerybridge/mediasync/AgentHook;->jobHook(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Z)Lde/robv/android/xposed/XC_MethodHook;

    move-result-object v0

    move-object/from16 v1, p8

    invoke-static {v1, v0}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    move-result-object v0

    invoke-virtual {v5, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 286
    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$9;
    :try_end_9
    .catchall {:try_start_9 .. :try_end_9} :catchall_1

    move-object/from16 v1, p10

    move-object/from16 v2, p11

    move-object/from16 v16, v4

    move-object/from16 v4, p12

    .end local v4    # "scope":Ljava/lang/ThreadLocal;, "Ljava/lang/ThreadLocal<Ldev/gallerybridge/mediasync/AgentHook$GateScope;>;"
    .local v16, "scope":Ljava/lang/ThreadLocal;, "Ljava/lang/ThreadLocal<Ldev/gallerybridge/mediasync/AgentHook$GateScope;>;"
    :try_start_a
    invoke-direct {v0, v3, v1, v4, v2}, Ldev/gallerybridge/mediasync/AgentHook$9;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Ljava/lang/Class;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V

    move-object/from16 v1, p9

    invoke-static {v1, v0}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    move-result-object v0

    invoke-virtual {v5, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 305
    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$10;

    invoke-direct {v0, v3}, Ldev/gallerybridge/mediasync/AgentHook$10;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V

    move-object/from16 v1, p13

    invoke-static {v1, v0}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    move-result-object v0

    invoke-virtual {v5, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 310
    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$11;

    invoke-direct {v0, v3}, Ldev/gallerybridge/mediasync/AgentHook$11;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;)V

    move-object/from16 v1, p14

    invoke-static {v1, v0}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    move-result-object v0

    invoke-virtual {v5, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 317
    invoke-virtual {v3}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->start()V

    .line 318
    const-string v0, "GB-MediaSync"

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "profile=1.6.09.34; hooks="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v5}, Ljava/util/ArrayList;->size()I

    move-result v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, "; sdk_guard=bounded"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_a
    .catchall {:try_start_a .. :try_end_a} :catchall_0

    .line 323
    nop

    .line 324
    return-void

    .line 319
    :catchall_0
    move-exception v0

    goto :goto_7

    .end local v16    # "scope":Ljava/lang/ThreadLocal;, "Ljava/lang/ThreadLocal<Ldev/gallerybridge/mediasync/AgentHook$GateScope;>;"
    .restart local v4    # "scope":Ljava/lang/ThreadLocal;, "Ljava/lang/ThreadLocal<Ldev/gallerybridge/mediasync/AgentHook$GateScope;>;"
    :catchall_1
    move-exception v0

    goto :goto_6

    :catchall_2
    move-exception v0

    goto :goto_0

    :catchall_3
    move-exception v0

    move-object/from16 v15, p6

    :goto_0
    move-object/from16 v6, p7

    goto :goto_6

    :catchall_4
    move-exception v0

    move-object/from16 v15, p6

    move-object/from16 v6, p7

    goto :goto_5

    :catchall_5
    move-exception v0

    move-object/from16 v15, p6

    move-object/from16 v6, p7

    move-object/from16 v13, p15

    goto :goto_5

    :catchall_6
    move-exception v0

    goto :goto_4

    :catchall_7
    move-exception v0

    goto :goto_3

    :catchall_8
    move-exception v0

    goto :goto_2

    :catchall_9
    move-exception v0

    goto :goto_1

    :catchall_a
    move-exception v0

    move-object/from16 v7, p1

    :goto_1
    move-object/from16 v8, p2

    :goto_2
    move-object/from16 v9, p3

    :goto_3
    move-object/from16 v10, p4

    :goto_4
    move-object/from16 v15, p6

    move-object/from16 v6, p7

    move-object/from16 v13, p15

    move-object/from16 v11, p16

    move-object/from16 v12, p17

    :goto_5
    move-object/from16 v14, p18

    :goto_6
    move-object/from16 v16, v4

    move-object/from16 v4, p12

    .line 320
    .end local v4    # "scope":Ljava/lang/ThreadLocal;, "Ljava/lang/ThreadLocal<Ldev/gallerybridge/mediasync/AgentHook$GateScope;>;"
    .local v0, "error":Ljava/lang/Throwable;
    .restart local v16    # "scope":Ljava/lang/ThreadLocal;, "Ljava/lang/ThreadLocal<Ldev/gallerybridge/mediasync/AgentHook$GateScope;>;"
    :goto_7
    const/4 v1, 0x1

    invoke-static {v3, v1}, Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;->-$$Nest$fputlatched(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Z)V

    .line 321
    invoke-virtual {v5}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_8
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_0

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lde/robv/android/xposed/XC_MethodHook$Unhook;

    .local v2, "hook":Lde/robv/android/xposed/XC_MethodHook$Unhook;
    invoke-virtual {v2}, Lde/robv/android/xposed/XC_MethodHook$Unhook;->unhook()V

    .end local v2    # "hook":Lde/robv/android/xposed/XC_MethodHook$Unhook;
    goto :goto_8

    .line 322
    :cond_0
    throw v0
.end method

.method private static integerField(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;
    .locals 4
    .param p1, "name"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Class<",
            "*>;",
            "Ljava/lang/String;",
            ")",
            "Ljava/lang/reflect/Field;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 167
    .local p0, "type":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    invoke-virtual {p0, p1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    move-result-object v0

    .line 168
    .local v0, "field":Ljava/lang/reflect/Field;
    invoke-virtual {v0}, Ljava/lang/reflect/Field;->getType()Ljava/lang/Class;

    move-result-object v1

    sget-object v2, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;

    if-ne v1, v2, :cond_0

    .line 169
    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->setAccessible(Z)V

    .line 170
    return-object v0

    .line 168
    :cond_0
    new-instance v1, Ljava/lang/NoSuchFieldException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {p0}, Ljava/lang/Class;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-string v3, "."

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/lang/NoSuchFieldException;-><init>(Ljava/lang/String;)V

    throw v1
.end method

.method private static jobHook(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Z)Lde/robv/android/xposed/XC_MethodHook;
    .locals 1
    .param p0, "state"    # Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;
    .param p1, "stop"    # Z

    .line 327
    new-instance v0, Ldev/gallerybridge/mediasync/AgentHook$12;

    invoke-direct {v0, p0, p1}, Ldev/gallerybridge/mediasync/AgentHook$12;-><init>(Ldev/gallerybridge/mediasync/AgentHook$RuntimeState;Z)V

    return-object v0
.end method

.method private static matches(Landroid/content/Context;Ljava/lang/String;[Ljava/lang/String;)Z
    .locals 18
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "basePath"    # Ljava/lang/String;
    .param p2, "splits"    # [Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 72
    move-object/from16 v1, p2

    invoke-virtual/range {p0 .. p0}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v0

    const-string v2, "com.samsung.android.agent.storage"

    const/4 v3, 0x0

    invoke-virtual {v0, v2, v3}, Landroid/content/pm/PackageManager;->getPackageInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;

    move-result-object v2

    .line 73
    .local v2, "info":Landroid/content/pm/PackageInfo;
    invoke-virtual {v2}, Landroid/content/pm/PackageInfo;->getLongVersionCode()J

    move-result-wide v4

    const-wide/32 v6, 0x99723c2

    cmp-long v0, v4, v6

    if-nez v0, :cond_f

    const-string v0, "1.6.09.34"

    iget-object v4, v2, Landroid/content/pm/PackageInfo;->versionName:Ljava/lang/String;

    invoke-virtual {v0, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_0

    move-object/from16 v4, p1

    move/from16 v17, v3

    goto/16 :goto_9

    .line 74
    :cond_0
    if-eqz v1, :cond_2

    array-length v0, v1

    move v4, v3

    :goto_0
    if-ge v4, v0, :cond_2

    aget-object v5, v1, v4

    .line 75
    .local v5, "split":Ljava/lang/String;
    new-instance v6, Ljava/util/zip/ZipFile;

    invoke-direct {v6, v5}, Ljava/util/zip/ZipFile;-><init>(Ljava/lang/String;)V

    .line 76
    .local v6, "archive":Ljava/util/zip/ZipFile;
    :try_start_0
    invoke-static {v6, v3}, Ldev/gallerybridge/mediasync/AgentHook;->dexNamesMatch(Ljava/util/zip/ZipFile;I)Z

    move-result v7
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    if-nez v7, :cond_1

    .line 77
    invoke-virtual {v6}, Ljava/util/zip/ZipFile;->close()V

    .line 76
    return v3

    .line 77
    :cond_1
    invoke-virtual {v6}, Ljava/util/zip/ZipFile;->close()V

    .line 74
    .end local v5    # "split":Ljava/lang/String;
    .end local v6    # "archive":Ljava/util/zip/ZipFile;
    add-int/lit8 v4, v4, 0x1

    goto :goto_0

    .line 75
    .restart local v5    # "split":Ljava/lang/String;
    .restart local v6    # "archive":Ljava/util/zip/ZipFile;
    :catchall_0
    move-exception v0

    move-object v3, v0

    :try_start_1
    invoke-virtual {v6}, Ljava/util/zip/ZipFile;->close()V
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    goto :goto_1

    :catchall_1
    move-exception v0

    invoke-virtual {v3, v0}, Ljava/lang/Throwable;->addSuppressed(Ljava/lang/Throwable;)V

    :goto_1
    throw v3

    .line 79
    .end local v5    # "split":Ljava/lang/String;
    .end local v6    # "archive":Ljava/util/zip/ZipFile;
    :cond_2
    new-instance v0, Ljava/util/zip/ZipFile;

    move-object/from16 v4, p1

    invoke-direct {v0, v4}, Ljava/util/zip/ZipFile;-><init>(Ljava/lang/String;)V

    move-object v5, v0

    .line 80
    .local v5, "zip":Ljava/util/zip/ZipFile;
    const/4 v0, 0x2

    :try_start_2
    invoke-static {v5, v0}, Ldev/gallerybridge/mediasync/AgentHook;->dexNamesMatch(Ljava/util/zip/ZipFile;I)Z

    move-result v6
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_4

    if-nez v6, :cond_3

    .line 102
    invoke-virtual {v5}, Ljava/util/zip/ZipFile;->close()V

    .line 80
    return v3

    .line 81
    :cond_3
    :try_start_3
    sget-object v6, Ldev/gallerybridge/mediasync/AgentHook;->DIGESTS:Ljava/util/Map;

    invoke-interface {v6}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v6

    invoke-interface {v6}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v6

    :goto_2
    invoke-interface {v6}, Ljava/util/Iterator;->hasNext()Z

    move-result v7

    if-eqz v7, :cond_e

    invoke-interface {v6}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Ljava/util/Map$Entry;

    .line 82
    .local v7, "expected":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;"
    invoke-interface {v7}, Ljava/util/Map$Entry;->getKey()Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Ljava/lang/String;

    invoke-virtual {v5, v8}, Ljava/util/zip/ZipFile;->getEntry(Ljava/lang/String;)Ljava/util/zip/ZipEntry;

    move-result-object v8

    .line 83
    .local v8, "entry":Ljava/util/zip/ZipEntry;
    if-eqz v8, :cond_d

    invoke-virtual {v8}, Ljava/util/zip/ZipEntry;->getSize()J

    move-result-wide v9

    const-wide/16 v11, 0x0

    cmp-long v9, v9, v11

    if-ltz v9, :cond_d

    invoke-virtual {v8}, Ljava/util/zip/ZipEntry;->getSize()J

    move-result-wide v9

    const-wide/32 v11, 0x1000000

    cmp-long v9, v9, v11

    if-lez v9, :cond_4

    move/from16 v17, v3

    goto/16 :goto_7

    .line 84
    :cond_4
    const-string v9, "SHA-256"

    invoke-static {v9}, Ljava/security/MessageDigest;->getInstance(Ljava/lang/String;)Ljava/security/MessageDigest;

    move-result-object v9

    .line 85
    .local v9, "hash":Ljava/security/MessageDigest;
    const-wide/16 v10, 0x0

    .line 86
    .local v10, "count":J
    invoke-virtual {v5, v8}, Ljava/util/zip/ZipFile;->getInputStream(Ljava/util/zip/ZipEntry;)Ljava/io/InputStream;

    move-result-object v12
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_4

    .line 87
    .local v12, "input":Ljava/io/InputStream;
    const/high16 v13, 0x20000

    :try_start_4
    new-array v13, v13, [B

    .line 89
    .local v13, "buffer":[B
    :goto_3
    invoke-virtual {v12, v13}, Ljava/io/InputStream;->read([B)I

    move-result v14

    move v15, v14

    .local v15, "bytes":I
    if-ltz v14, :cond_7

    .line 90
    move v14, v0

    int-to-long v0, v15

    add-long/2addr v10, v0

    .line 91
    invoke-virtual {v8}, Ljava/util/zip/ZipEntry;->getSize()J

    move-result-wide v0
    :try_end_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_2

    cmp-long v0, v10, v0

    if-lez v0, :cond_6

    .line 94
    if-eqz v12, :cond_5

    :try_start_5
    invoke-virtual {v12}, Ljava/io/InputStream;->close()V
    :try_end_5
    .catchall {:try_start_5 .. :try_end_5} :catchall_4

    .line 102
    :cond_5
    invoke-virtual {v5}, Ljava/util/zip/ZipFile;->close()V

    .line 91
    return v3

    .line 92
    :cond_6
    :try_start_6
    invoke-virtual {v9, v13, v3, v15}, Ljava/security/MessageDigest;->update([BII)V
    :try_end_6
    .catchall {:try_start_6 .. :try_end_6} :catchall_2

    move-object/from16 v1, p2

    move v0, v14

    goto :goto_3

    .line 89
    :cond_7
    move v14, v0

    .line 94
    .end local v13    # "buffer":[B
    .end local v15    # "bytes":I
    if-eqz v12, :cond_8

    :try_start_7
    invoke-virtual {v12}, Ljava/io/InputStream;->close()V

    .line 95
    .end local v12    # "input":Ljava/io/InputStream;
    :cond_8
    invoke-virtual {v9}, Ljava/security/MessageDigest;->digest()[B

    move-result-object v0

    .line 96
    .local v0, "actual":[B
    const/16 v1, 0x20

    new-array v1, v1, [B

    .line 97
    .local v1, "known":[B
    const/4 v12, 0x0

    .local v12, "i":I
    :goto_4
    array-length v13, v1

    if-ge v12, v13, :cond_9

    .line 98
    invoke-interface {v7}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v13

    check-cast v13, Ljava/lang/String;

    mul-int/lit8 v15, v12, 0x2

    mul-int/lit8 v16, v12, 0x2

    move/from16 v17, v3

    add-int/lit8 v3, v16, 0x2

    invoke-virtual {v13, v15, v3}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v3

    const/16 v13, 0x10

    invoke-static {v3, v13}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;I)I

    move-result v3

    int-to-byte v3, v3

    aput-byte v3, v1, v12

    .line 97
    add-int/lit8 v12, v12, 0x1

    move/from16 v3, v17

    goto :goto_4

    :cond_9
    move/from16 v17, v3

    .line 100
    .end local v12    # "i":I
    invoke-virtual {v8}, Ljava/util/zip/ZipEntry;->getSize()J

    move-result-wide v12

    cmp-long v3, v10, v12

    if-nez v3, :cond_b

    invoke-static {v0, v1}, Ljava/security/MessageDigest;->isEqual([B[B)Z

    move-result v3
    :try_end_7
    .catchall {:try_start_7 .. :try_end_7} :catchall_4

    if-nez v3, :cond_a

    goto :goto_5

    .line 101
    .end local v0    # "actual":[B
    .end local v1    # "known":[B
    .end local v7    # "expected":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;"
    .end local v8    # "entry":Ljava/util/zip/ZipEntry;
    .end local v9    # "hash":Ljava/security/MessageDigest;
    .end local v10    # "count":J
    :cond_a
    move-object/from16 v1, p2

    move v0, v14

    move/from16 v3, v17

    goto/16 :goto_2

    .line 102
    .restart local v0    # "actual":[B
    .restart local v1    # "known":[B
    .restart local v7    # "expected":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;"
    .restart local v8    # "entry":Ljava/util/zip/ZipEntry;
    .restart local v9    # "hash":Ljava/security/MessageDigest;
    .restart local v10    # "count":J
    :cond_b
    :goto_5
    invoke-virtual {v5}, Ljava/util/zip/ZipFile;->close()V

    .line 100
    return v17

    .line 86
    .end local v0    # "actual":[B
    .end local v1    # "known":[B
    .local v12, "input":Ljava/io/InputStream;
    :catchall_2
    move-exception v0

    move-object v1, v0

    if-eqz v12, :cond_c

    :try_start_8
    invoke-virtual {v12}, Ljava/io/InputStream;->close()V
    :try_end_8
    .catchall {:try_start_8 .. :try_end_8} :catchall_3

    goto :goto_6

    :catchall_3
    move-exception v0

    :try_start_9
    invoke-virtual {v1, v0}, Ljava/lang/Throwable;->addSuppressed(Ljava/lang/Throwable;)V

    .end local v2    # "info":Landroid/content/pm/PackageInfo;
    .end local v5    # "zip":Ljava/util/zip/ZipFile;
    .end local p0    # "context":Landroid/content/Context;
    .end local p1    # "basePath":Ljava/lang/String;
    .end local p2    # "splits":[Ljava/lang/String;
    :cond_c
    :goto_6
    throw v1
    :try_end_9
    .catchall {:try_start_9 .. :try_end_9} :catchall_4

    .line 83
    .end local v9    # "hash":Ljava/security/MessageDigest;
    .end local v10    # "count":J
    .end local v12    # "input":Ljava/io/InputStream;
    .restart local v2    # "info":Landroid/content/pm/PackageInfo;
    .restart local v5    # "zip":Ljava/util/zip/ZipFile;
    .restart local p0    # "context":Landroid/content/Context;
    .restart local p1    # "basePath":Ljava/lang/String;
    .restart local p2    # "splits":[Ljava/lang/String;
    :cond_d
    move/from16 v17, v3

    .line 102
    :goto_7
    invoke-virtual {v5}, Ljava/util/zip/ZipFile;->close()V

    .line 83
    return v17

    .line 102
    .end local v7    # "expected":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;"
    .end local v8    # "entry":Ljava/util/zip/ZipEntry;
    :cond_e
    invoke-virtual {v5}, Ljava/util/zip/ZipFile;->close()V

    .line 103
    .end local v5    # "zip":Ljava/util/zip/ZipFile;
    const/4 v0, 0x1

    return v0

    .line 79
    .restart local v5    # "zip":Ljava/util/zip/ZipFile;
    :catchall_4
    move-exception v0

    move-object v1, v0

    :try_start_a
    invoke-virtual {v5}, Ljava/util/zip/ZipFile;->close()V
    :try_end_a
    .catchall {:try_start_a .. :try_end_a} :catchall_5

    goto :goto_8

    :catchall_5
    move-exception v0

    invoke-virtual {v1, v0}, Ljava/lang/Throwable;->addSuppressed(Ljava/lang/Throwable;)V

    :goto_8
    throw v1

    .line 73
    .end local v5    # "zip":Ljava/util/zip/ZipFile;
    :cond_f
    move-object/from16 v4, p1

    move/from16 v17, v3

    :goto_9
    return v17
.end method

.method private static varargs method(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    .locals 4
    .param p1, "name"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Class<",
            "*>;",
            "Ljava/lang/String;",
            "Ljava/lang/Class<",
            "*>;[",
            "Ljava/lang/Class<",
            "*>;)",
            "Ljava/lang/reflect/Method;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/NoSuchMethodException;
        }
    .end annotation

    .line 124
    .local p0, "owner":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .local p2, "result":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .local p3, "parameters":[Ljava/lang/Class;, "[Ljava/lang/Class<*>;"
    invoke-virtual {p0, p1, p3}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    .line 125
    .local v0, "found":Ljava/lang/reflect/Method;
    invoke-virtual {v0}, Ljava/lang/reflect/Method;->getReturnType()Ljava/lang/Class;

    move-result-object v1

    if-ne v1, p2, :cond_0

    .line 126
    return-object v0

    .line 125
    :cond_0
    new-instance v1, Ljava/lang/NoSuchMethodException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {p0}, Ljava/lang/Class;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-string v3, "."

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/lang/NoSuchMethodException;-><init>(Ljava/lang/String;)V

    throw v1
.end method

.method private static type(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;
    .locals 1
    .param p0, "loader"    # Ljava/lang/ClassLoader;
    .param p1, "name"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/ClassLoader;",
            "Ljava/lang/String;",
            ")",
            "Ljava/lang/Class<",
            "*>;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/ClassNotFoundException;
        }
    .end annotation

    .line 119
    const/4 v0, 0x0

    invoke-static {p1, v0, p0}, Ljava/lang/Class;->forName(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;

    move-result-object v0

    return-object v0
.end method


# virtual methods
.method public handleLoadPackage(Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;)V
    .locals 4
    .param p1, "target"    # Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Throwable;
        }
    .end annotation

    .line 48
    iget-object v0, p1, Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;->packageName:Ljava/lang/String;

    const-string v1, "com.samsung.android.agent.storage"

    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_1

    iget-object v0, p1, Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;->processName:Ljava/lang/String;

    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_1

    iget-object v0, p1, Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;->appInfo:Landroid/content/pm/ApplicationInfo;

    if-nez v0, :cond_0

    goto :goto_0

    .line 49
    :cond_0
    const-class v0, Landroid/app/Application;

    const/4 v1, 0x1

    new-array v1, v1, [Ljava/lang/Class;

    const/4 v2, 0x0

    const-class v3, Landroid/content/Context;

    aput-object v3, v1, v2

    const-string v2, "attach"

    invoke-virtual {v0, v2, v1}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    .line 50
    .local v0, "attach":Ljava/lang/reflect/Method;
    new-instance v1, Ldev/gallerybridge/mediasync/AgentHook$1;

    invoke-direct {v1, p0, p1}, Ldev/gallerybridge/mediasync/AgentHook$1;-><init>(Ldev/gallerybridge/mediasync/AgentHook;Lde/robv/android/xposed/callbacks/XC_LoadPackage$LoadPackageParam;)V

    invoke-static {v0, v1}, Lde/robv/android/xposed/XposedBridge;->hookMethod(Ljava/lang/reflect/Member;Lde/robv/android/xposed/XC_MethodHook;)Lde/robv/android/xposed/XC_MethodHook$Unhook;

    .line 68
    return-void

    .line 48
    .end local v0    # "attach":Ljava/lang/reflect/Method;
    :cond_1
    :goto_0
    return-void
.end method

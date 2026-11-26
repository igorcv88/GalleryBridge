.class public final Ldev/gallerybridge/mediasync/PolicyActivity;
.super Landroid/app/Activity;
.source "PolicyActivity.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 16
    invoke-direct {p0}, Landroid/app/Activity;-><init>()V

    return-void
.end method

.method private check(Landroid/widget/LinearLayout;Ljava/lang/String;Z)Landroid/widget/CheckBox;
    .locals 1
    .param p1, "parent"    # Landroid/widget/LinearLayout;
    .param p2, "text"    # Ljava/lang/String;
    .param p3, "enabled"    # Z

    .line 71
    new-instance v0, Landroid/widget/CheckBox;

    invoke-direct {v0, p0}, Landroid/widget/CheckBox;-><init>(Landroid/content/Context;)V

    .line 72
    .local v0, "view":Landroid/widget/CheckBox;
    invoke-virtual {v0, p2}, Landroid/widget/CheckBox;->setText(Ljava/lang/CharSequence;)V

    .line 73
    invoke-virtual {v0, p3}, Landroid/widget/CheckBox;->setChecked(Z)V

    .line 74
    invoke-virtual {p1, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 75
    return-object v0
.end method

.method private label(Landroid/widget/LinearLayout;Ljava/lang/String;)Landroid/widget/TextView;
    .locals 1
    .param p1, "parent"    # Landroid/widget/LinearLayout;
    .param p2, "text"    # Ljava/lang/String;

    .line 64
    new-instance v0, Landroid/widget/TextView;

    invoke-direct {v0, p0}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    .line 65
    .local v0, "view":Landroid/widget/TextView;
    invoke-virtual {v0, p2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 66
    invoke-virtual {p1, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 67
    return-object v0
.end method

.method static synthetic lambda$onCreate$0(Landroid/content/SharedPreferences;Landroid/widget/Spinner;Landroid/widget/CheckBox;Landroid/widget/CheckBox;Landroid/widget/CheckBox;Landroid/widget/SeekBar;Landroid/widget/SeekBar;Landroid/widget/TextView;Landroid/view/View;)V
    .locals 3
    .param p0, "prefs"    # Landroid/content/SharedPreferences;
    .param p1, "mode"    # Landroid/widget/Spinner;
    .param p2, "thermal"    # Landroid/widget/CheckBox;
    .param p3, "battery"    # Landroid/widget/CheckBox;
    .param p4, "diagnostics"    # Landroid/widget/CheckBox;
    .param p5, "heat"    # Landroid/widget/SeekBar;
    .param p6, "charge"    # Landroid/widget/SeekBar;
    .param p7, "status"    # Landroid/widget/TextView;
    .param p8, "v"    # Landroid/view/View;

    .line 54
    invoke-interface {p0}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    const-string v1, "schema"

    const/4 v2, 0x1

    invoke-interface {v0, v1, v2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    const-string v1, "profile"

    invoke-virtual {p1}, Landroid/widget/Spinner;->getSelectedItemPosition()I

    move-result v2

    invoke-interface {v0, v1, v2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    .line 55
    invoke-virtual {p2}, Landroid/widget/CheckBox;->isChecked()Z

    move-result v1

    const-string v2, "thermal"

    invoke-interface {v0, v2, v1}, Landroid/content/SharedPreferences$Editor;->putBoolean(Ljava/lang/String;Z)Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    const-string v1, "battery"

    invoke-virtual {p3}, Landroid/widget/CheckBox;->isChecked()Z

    move-result v2

    invoke-interface {v0, v1, v2}, Landroid/content/SharedPreferences$Editor;->putBoolean(Ljava/lang/String;Z)Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    .line 56
    invoke-virtual {p4}, Landroid/widget/CheckBox;->isChecked()Z

    move-result v1

    const-string v2, "diag"

    invoke-interface {v0, v2, v1}, Landroid/content/SharedPreferences$Editor;->putBoolean(Ljava/lang/String;Z)Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    invoke-virtual {p5}, Landroid/widget/SeekBar;->getProgress()I

    move-result v1

    add-int/lit8 v1, v1, 0x37

    const-string v2, "limit"

    invoke-interface {v0, v2, v1}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    .line 57
    invoke-virtual {p6}, Landroid/widget/SeekBar;->getProgress()I

    move-result v1

    add-int/lit8 v1, v1, 0xa

    const-string v2, "min"

    invoke-interface {v0, v2, v1}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    invoke-interface {v0}, Landroid/content/SharedPreferences$Editor;->commit()Z

    move-result v0

    .line 58
    .local v0, "written":Z
    if-eqz v0, :cond_0

    const-string v1, "Pol\u00edtica salva. Consulte o registro GB-MediaSync durante o teste."

    goto :goto_0

    :cond_0
    const-string v1, "Grava\u00e7\u00e3o recusada."

    :goto_0
    invoke-virtual {p7, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 59
    return-void
.end method

.method private slider(Landroid/widget/LinearLayout;Ljava/lang/String;III)Landroid/widget/SeekBar;
    .locals 4
    .param p1, "parent"    # Landroid/widget/LinearLayout;
    .param p2, "description"    # Ljava/lang/String;
    .param p3, "min"    # I
    .param p4, "max"    # I
    .param p5, "value"    # I

    .line 79
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, " \u00b7 "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, p5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-direct {p0, p1, v0}, Ldev/gallerybridge/mediasync/PolicyActivity;->label(Landroid/widget/LinearLayout;Ljava/lang/String;)Landroid/widget/TextView;

    move-result-object v0

    .line 80
    .local v0, "label":Landroid/widget/TextView;
    new-instance v1, Landroid/widget/SeekBar;

    invoke-direct {v1, p0}, Landroid/widget/SeekBar;-><init>(Landroid/content/Context;)V

    .line 81
    .local v1, "bar":Landroid/widget/SeekBar;
    sub-int v2, p4, p3

    invoke-virtual {v1, v2}, Landroid/widget/SeekBar;->setMax(I)V

    .line 82
    sub-int v2, p4, p3

    sub-int v3, p5, p3

    invoke-static {v2, v3}, Ljava/lang/Math;->min(II)I

    move-result v2

    const/4 v3, 0x0

    invoke-static {v3, v2}, Ljava/lang/Math;->max(II)I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/widget/SeekBar;->setProgress(I)V

    .line 83
    invoke-virtual {v1, p2}, Landroid/widget/SeekBar;->setContentDescription(Ljava/lang/CharSequence;)V

    .line 84
    new-instance v2, Ldev/gallerybridge/mediasync/PolicyActivity$1;

    invoke-direct {v2, p0, v0, p2, p3}, Ldev/gallerybridge/mediasync/PolicyActivity$1;-><init>(Ldev/gallerybridge/mediasync/PolicyActivity;Landroid/widget/TextView;Ljava/lang/String;I)V

    invoke-virtual {v1, v2}, Landroid/widget/SeekBar;->setOnSeekBarChangeListener(Landroid/widget/SeekBar$OnSeekBarChangeListener;)V

    .line 91
    invoke-virtual {p1, v1}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 92
    return-object v1
.end method


# virtual methods
.method public onCreate(Landroid/os/Bundle;)V
    .locals 18
    .param p1, "state"    # Landroid/os/Bundle;

    .line 19
    move-object/from16 v1, p0

    invoke-super/range {p0 .. p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V

    .line 20
    new-instance v2, Landroid/widget/LinearLayout;

    invoke-direct {v2, v1}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;)V

    .line 21
    .local v2, "page":Landroid/widget/LinearLayout;
    const/4 v0, 0x1

    invoke-virtual {v2, v0}, Landroid/widget/LinearLayout;->setOrientation(I)V

    .line 22
    invoke-virtual {v1}, Ldev/gallerybridge/mediasync/PolicyActivity;->getResources()Landroid/content/res/Resources;

    move-result-object v3

    invoke-virtual {v3}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    move-result-object v3

    iget v3, v3, Landroid/util/DisplayMetrics;->density:F

    const/high16 v4, 0x41a00000    # 20.0f

    mul-float/2addr v3, v4

    float-to-int v7, v3

    .line 23
    .local v7, "margin":I
    invoke-virtual {v2, v7, v7, v7, v7}, Landroid/widget/LinearLayout;->setPadding(IIII)V

    .line 24
    new-instance v3, Landroid/widget/ScrollView;

    invoke-direct {v3, v1}, Landroid/widget/ScrollView;-><init>(Landroid/content/Context;)V

    move-object v8, v3

    .line 25
    .local v8, "scroll":Landroid/widget/ScrollView;
    invoke-virtual {v8, v2}, Landroid/widget/ScrollView;->addView(Landroid/view/View;)V

    .line 26
    invoke-virtual {v1, v8}, Ldev/gallerybridge/mediasync/PolicyActivity;->setContentView(Landroid/view/View;)V

    .line 30
    :try_start_0
    const-string v3, "policy"

    invoke-virtual {v1, v3, v0}, Ldev/gallerybridge/mediasync/PolicyActivity;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v3
    :try_end_0
    .catch Ljava/lang/SecurityException; {:try_start_0 .. :try_end_0} :catch_0

    move-object v10, v3

    .line 34
    .local v10, "prefs":Landroid/content/SharedPreferences;
    nop

    .line 35
    const-string v3, "GalleryBridge MediaSync \u2022 perfil experimental"

    invoke-direct {v1, v2, v3}, Ldev/gallerybridge/mediasync/PolicyActivity;->label(Landroid/widget/LinearLayout;Ljava/lang/String;)Landroid/widget/TextView;

    .line 36
    const-string v3, "Atua\u00e7\u00e3o limitada ao Storage Agent 1.6.09.34 com DEX verificado. O perfil conservador admite bateria baixa somente em carga. O personalizado permite configurar bateria e temperatura."

    invoke-direct {v1, v2, v3}, Ldev/gallerybridge/mediasync/PolicyActivity;->label(Landroid/widget/LinearLayout;Ljava/lang/String;)Landroid/widget/TextView;

    .line 37
    new-instance v11, Landroid/widget/Spinner;

    invoke-direct {v11, v1}, Landroid/widget/Spinner;-><init>(Landroid/content/Context;)V

    .line 38
    .local v11, "mode":Landroid/widget/Spinner;
    const/4 v3, 0x3

    new-array v3, v3, [Ljava/lang/String;

    const-string v4, "Desativado"

    const/4 v5, 0x0

    aput-object v4, v3, v5

    const-string v4, "Conservador"

    aput-object v4, v3, v0

    const-string v0, "Personalizado"

    const/4 v4, 0x2

    aput-object v0, v3, v4

    move-object v0, v3

    .line 39
    .local v0, "values":[Ljava/lang/String;
    new-instance v3, Landroid/widget/ArrayAdapter;

    const v6, 0x1090008

    invoke-direct {v3, v1, v6, v0}, Landroid/widget/ArrayAdapter;-><init>(Landroid/content/Context;I[Ljava/lang/Object;)V

    move-object v9, v3

    .line 40
    .local v9, "adapter":Landroid/widget/ArrayAdapter;, "Landroid/widget/ArrayAdapter<Ljava/lang/String;>;"
    const v3, 0x1090009

    invoke-virtual {v9, v3}, Landroid/widget/ArrayAdapter;->setDropDownViewResource(I)V

    .line 41
    invoke-virtual {v11, v9}, Landroid/widget/Spinner;->setAdapter(Landroid/widget/SpinnerAdapter;)V

    .line 42
    const-string v3, "profile"

    invoke-interface {v10, v3, v5}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v3

    invoke-static {v4, v3}, Ljava/lang/Math;->min(II)I

    move-result v3

    invoke-static {v5, v3}, Ljava/lang/Math;->max(II)I

    move-result v3

    invoke-virtual {v11, v3}, Landroid/widget/Spinner;->setSelection(I)V

    .line 43
    invoke-virtual {v2, v11}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 44
    const-string v3, "thermal"

    invoke-interface {v10, v3, v5}, Landroid/content/SharedPreferences;->getBoolean(Ljava/lang/String;Z)Z

    move-result v3

    const-string v4, "Permitir a decis\u00e3o t\u00e9rmica Samsung em MODERATE"

    invoke-direct {v1, v2, v4, v3}, Ldev/gallerybridge/mediasync/PolicyActivity;->check(Landroid/widget/LinearLayout;Ljava/lang/String;Z)Landroid/widget/CheckBox;

    move-result-object v12

    .line 45
    .local v12, "thermal":Landroid/widget/CheckBox;
    const-string v3, "battery"

    invoke-interface {v10, v3, v5}, Landroid/content/SharedPreferences;->getBoolean(Ljava/lang/String;Z)Z

    move-result v3

    const-string v4, "Permitir a decis\u00e3o de bateria baixa em carga real"

    invoke-direct {v1, v2, v4, v3}, Ldev/gallerybridge/mediasync/PolicyActivity;->check(Landroid/widget/LinearLayout;Ljava/lang/String;Z)Landroid/widget/CheckBox;

    move-result-object v13

    .line 46
    .local v13, "battery":Landroid/widget/CheckBox;
    const-string v3, "diag"

    invoke-interface {v10, v3, v5}, Landroid/content/SharedPreferences;->getBoolean(Ljava/lang/String;Z)Z

    move-result v3

    const-string v4, "Registrar decis\u00f5es sem identificadores no logcat"

    invoke-direct {v1, v2, v4, v3}, Ldev/gallerybridge/mediasync/PolicyActivity;->check(Landroid/widget/LinearLayout;Ljava/lang/String;Z)Landroid/widget/CheckBox;

    move-result-object v14

    .line 47
    .local v14, "diagnostics":Landroid/widget/CheckBox;
    const-string v3, "limit"

    const/16 v4, 0x3c

    invoke-interface {v10, v3, v4}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v6

    const-string v3, "Limite da previs\u00e3o em % do limiar SEVERE"

    const/16 v4, 0x37

    const/16 v5, 0x4b

    invoke-direct/range {v1 .. v6}, Ldev/gallerybridge/mediasync/PolicyActivity;->slider(Landroid/widget/LinearLayout;Ljava/lang/String;III)Landroid/widget/SeekBar;

    move-result-object v15

    .line 48
    .local v15, "heat":Landroid/widget/SeekBar;
    const-string v1, "min"

    const/16 v3, 0xf

    invoke-interface {v10, v1, v3}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v6

    const-string v3, "Bateria m\u00ednima em %"

    const/16 v4, 0xa

    const/16 v5, 0x1e

    move-object/from16 v1, p0

    invoke-direct/range {v1 .. v6}, Ldev/gallerybridge/mediasync/PolicyActivity;->slider(Landroid/widget/LinearLayout;Ljava/lang/String;III)Landroid/widget/SeekBar;

    move-result-object v16

    .line 49
    .local v16, "charge":Landroid/widget/SeekBar;
    const-string v3, "Em condi\u00e7\u00f5es severas, a interven\u00e7\u00e3o trava at\u00e9 reiniciar o processo do agente. O cancelamento t\u00e9rmico nativo permanece ativo quando a margem deixa de ser v\u00e1lida. O JobScheduler conserva suas restri\u00e7\u00f5es."

    invoke-direct {v1, v2, v3}, Ldev/gallerybridge/mediasync/PolicyActivity;->label(Landroid/widget/LinearLayout;Ljava/lang/String;)Landroid/widget/TextView;

    .line 50
    const-string v3, ""

    invoke-direct {v1, v2, v3}, Ldev/gallerybridge/mediasync/PolicyActivity;->label(Landroid/widget/LinearLayout;Ljava/lang/String;)Landroid/widget/TextView;

    move-result-object v17

    .line 51
    .local v17, "status":Landroid/widget/TextView;
    new-instance v3, Landroid/widget/Button;

    invoke-direct {v3, v1}, Landroid/widget/Button;-><init>(Landroid/content/Context;)V

    .line 52
    .local v3, "save":Landroid/widget/Button;
    const-string v4, "Salvar pol\u00edtica"

    invoke-virtual {v3, v4}, Landroid/widget/Button;->setText(Ljava/lang/CharSequence;)V

    .line 53
    move-object v4, v9

    .end local v9    # "adapter":Landroid/widget/ArrayAdapter;, "Landroid/widget/ArrayAdapter<Ljava/lang/String;>;"
    .local v4, "adapter":Landroid/widget/ArrayAdapter;, "Landroid/widget/ArrayAdapter<Ljava/lang/String;>;"
    new-instance v9, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;

    invoke-direct/range {v9 .. v17}, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;-><init>(Landroid/content/SharedPreferences;Landroid/widget/Spinner;Landroid/widget/CheckBox;Landroid/widget/CheckBox;Landroid/widget/CheckBox;Landroid/widget/SeekBar;Landroid/widget/SeekBar;Landroid/widget/TextView;)V

    invoke-virtual {v3, v9}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 60
    new-instance v5, Landroid/widget/LinearLayout$LayoutParams;

    const/4 v6, -0x1

    const/4 v9, -0x2

    invoke-direct {v5, v6, v9}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    invoke-virtual {v2, v3, v5}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 61
    return-void

    .line 31
    .end local v0    # "values":[Ljava/lang/String;
    .end local v3    # "save":Landroid/widget/Button;
    .end local v4    # "adapter":Landroid/widget/ArrayAdapter;, "Landroid/widget/ArrayAdapter<Ljava/lang/String;>;"
    .end local v10    # "prefs":Landroid/content/SharedPreferences;
    .end local v11    # "mode":Landroid/widget/Spinner;
    .end local v12    # "thermal":Landroid/widget/CheckBox;
    .end local v13    # "battery":Landroid/widget/CheckBox;
    .end local v14    # "diagnostics":Landroid/widget/CheckBox;
    .end local v15    # "heat":Landroid/widget/SeekBar;
    .end local v16    # "charge":Landroid/widget/SeekBar;
    .end local v17    # "status":Landroid/widget/TextView;
    :catch_0
    move-exception v0

    .line 32
    .local v0, "unavailable":Ljava/lang/SecurityException;
    const-string v3, "O LSPosed ainda n\u00e3o disponibiliza as prefer\u00eancias compartilhadas. Habilite este m\u00f3dulo e abra a tela de novo."

    invoke-direct {v1, v2, v3}, Ldev/gallerybridge/mediasync/PolicyActivity;->label(Landroid/widget/LinearLayout;Ljava/lang/String;)Landroid/widget/TextView;

    .line 33
    return-void
.end method

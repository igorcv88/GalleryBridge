.class public final synthetic Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Landroid/view/View$OnClickListener;


# instance fields
.field public final synthetic f$0:Landroid/content/SharedPreferences;

.field public final synthetic f$1:Landroid/widget/Spinner;

.field public final synthetic f$2:Landroid/widget/CheckBox;

.field public final synthetic f$3:Landroid/widget/CheckBox;

.field public final synthetic f$4:Landroid/widget/CheckBox;

.field public final synthetic f$5:Landroid/widget/SeekBar;

.field public final synthetic f$6:Landroid/widget/SeekBar;

.field public final synthetic f$7:Landroid/widget/TextView;


# direct methods
.method public synthetic constructor <init>(Landroid/content/SharedPreferences;Landroid/widget/Spinner;Landroid/widget/CheckBox;Landroid/widget/CheckBox;Landroid/widget/CheckBox;Landroid/widget/SeekBar;Landroid/widget/SeekBar;Landroid/widget/TextView;)V
    .locals 0

    .line 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$0:Landroid/content/SharedPreferences;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$1:Landroid/widget/Spinner;

    iput-object p3, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$2:Landroid/widget/CheckBox;

    iput-object p4, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$3:Landroid/widget/CheckBox;

    iput-object p5, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$4:Landroid/widget/CheckBox;

    iput-object p6, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$5:Landroid/widget/SeekBar;

    iput-object p7, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$6:Landroid/widget/SeekBar;

    iput-object p8, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$7:Landroid/widget/TextView;

    return-void
.end method


# virtual methods
.method public final onClick(Landroid/view/View;)V
    .locals 9

    .line 0
    iget-object v0, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$0:Landroid/content/SharedPreferences;

    iget-object v1, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$1:Landroid/widget/Spinner;

    iget-object v2, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$2:Landroid/widget/CheckBox;

    iget-object v3, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$3:Landroid/widget/CheckBox;

    iget-object v4, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$4:Landroid/widget/CheckBox;

    iget-object v5, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$5:Landroid/widget/SeekBar;

    iget-object v6, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$6:Landroid/widget/SeekBar;

    iget-object v7, p0, Ldev/gallerybridge/mediasync/PolicyActivity$$ExternalSyntheticLambda0;->f$7:Landroid/widget/TextView;

    move-object v8, p1

    invoke-static/range {v0 .. v8}, Ldev/gallerybridge/mediasync/PolicyActivity;->lambda$onCreate$0(Landroid/content/SharedPreferences;Landroid/widget/Spinner;Landroid/widget/CheckBox;Landroid/widget/CheckBox;Landroid/widget/CheckBox;Landroid/widget/SeekBar;Landroid/widget/SeekBar;Landroid/widget/TextView;Landroid/view/View;)V

    return-void
.end method

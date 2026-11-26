.class Ldev/gallerybridge/mediasync/PolicyActivity$1;
.super Ljava/lang/Object;
.source "PolicyActivity.java"

# interfaces
.implements Landroid/widget/SeekBar$OnSeekBarChangeListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Ldev/gallerybridge/mediasync/PolicyActivity;->slider(Landroid/widget/LinearLayout;Ljava/lang/String;III)Landroid/widget/SeekBar;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Ldev/gallerybridge/mediasync/PolicyActivity;

.field final synthetic val$description:Ljava/lang/String;

.field final synthetic val$label:Landroid/widget/TextView;

.field final synthetic val$min:I


# direct methods
.method constructor <init>(Ldev/gallerybridge/mediasync/PolicyActivity;Landroid/widget/TextView;Ljava/lang/String;I)V
    .locals 0
    .param p1, "this$0"    # Ldev/gallerybridge/mediasync/PolicyActivity;
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x8010,
            0x1010,
            0x1010,
            0x1010
        }
        names = {
            null,
            null,
            null,
            null
        }
    .end annotation

    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()V"
        }
    .end annotation

    .line 84
    iput-object p1, p0, Ldev/gallerybridge/mediasync/PolicyActivity$1;->this$0:Ldev/gallerybridge/mediasync/PolicyActivity;

    iput-object p2, p0, Ldev/gallerybridge/mediasync/PolicyActivity$1;->val$label:Landroid/widget/TextView;

    iput-object p3, p0, Ldev/gallerybridge/mediasync/PolicyActivity$1;->val$description:Ljava/lang/String;

    iput p4, p0, Ldev/gallerybridge/mediasync/PolicyActivity$1;->val$min:I

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onProgressChanged(Landroid/widget/SeekBar;IZ)V
    .locals 3
    .param p1, "ignored"    # Landroid/widget/SeekBar;
    .param p2, "offset"    # I
    .param p3, "fromUser"    # Z

    .line 88
    iget-object v0, p0, Ldev/gallerybridge/mediasync/PolicyActivity$1;->val$label:Landroid/widget/TextView;

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    iget-object v2, p0, Ldev/gallerybridge/mediasync/PolicyActivity$1;->val$description:Ljava/lang/String;

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, " \u00b7 "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget v2, p0, Ldev/gallerybridge/mediasync/PolicyActivity$1;->val$min:I

    add-int/2addr v2, p2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 89
    return-void
.end method

.method public onStartTrackingTouch(Landroid/widget/SeekBar;)V
    .locals 0
    .param p1, "ignored"    # Landroid/widget/SeekBar;

    .line 85
    return-void
.end method

.method public onStopTrackingTouch(Landroid/widget/SeekBar;)V
    .locals 0
    .param p1, "ignored"    # Landroid/widget/SeekBar;

    .line 86
    return-void
.end method

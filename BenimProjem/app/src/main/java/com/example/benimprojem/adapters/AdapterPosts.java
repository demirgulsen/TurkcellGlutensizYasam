package com.example.benimprojem.adapters;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benimprojem.AddPostActivity;
import com.example.benimprojem.PostDetailActivity;
import com.example.benimprojem.R;
import com.example.benimprojem.ThereProfileActivity;
import com.example.benimprojem.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts  extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    Context context;
    List<ModelPost> postList;

    String myUid;

    private DatabaseReference likesRef;
    private DatabaseReference postsRef;
    private DatabaseReference savesRef;


    boolean mProcessLike= false;
    boolean mProcessSave = false;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        savesRef = FirebaseDatabase.getInstance().getReference().child("Saves");

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        final String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        final String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescr = postList.get(position).getpDescr();
        final String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();
        String pLikes = postList.get(position).getpLikes();
        String pComments = postList.get(position).getpComments();  // containes total number of likes for a post
        //String pSaves = postList.get(position).getpSaved();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescrTv.setText(pDescr);
        holder.pLikesTv.setText(pLikes + "Likes");
        holder.pCommentsTv.setText(pComments + "Comments");



        setLikes(holder, pId);
        //setSaves(holder, pId);

        //set user dp
        try{
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_img).into(holder.uPictureIv);

        }catch (Exception e){
        }
        //set post image
        if (pImage.equals("noImage")){
            holder.pImageIv.setVisibility(View.GONE);
        }
        else{
            holder.pImageIv.setVisibility(View.VISIBLE);
            try{
                Picasso.get().load(pImage).into(holder.pImageIv);

            }catch (Exception e){

            }
        }

        //handle button click
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.moreBtn, uid, myUid, pId, pImage);
            }
        });


       //// saved işlemi
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final ModelPost post = postList.get(position);
        isSaved(post.getpId(), holder.save); // burası  farklı

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getpId()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getpId()).removeValue();
                }
            }

        });

//        holder.save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final int pSaves= Integer.parseInt(postList.get(position).getpSaved());
//                mProcessSave =true;
//                // gönderiye tıklandığında gelen id
//                final String postide = postList.get(position).getpId();
//                savesRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (mProcessSave){
//                            if (dataSnapshot.child(postide).hasChild(myUid)){
//                                //zatenn kaydedilmişse postu
//                                postsRef.child(postide).child("pSaves").setValue(""+(pSaves-1));
//                                savesRef.child(postide).child(myUid).removeValue();
//                                mProcessSave = false;
//                            }else{
//                                postsRef.child(postide).child("pSaves").setValue(""+(pSaves+1));
//                                savesRef.child(postide).child(myUid).setValue("Saves");
//                                mProcessSave = false;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//            }
//        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              final int pLikes = Integer.parseInt(postList.get(position).getpLikes());
              mProcessLike = true;
              final String postIde = postList.get(position).getpId();

              likesRef.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      if (mProcessLike){
                          if (dataSnapshot.child(postIde).hasChild(myUid)){
                              postsRef.child(postIde).child("pLikes").setValue(""+(pLikes-1));
                              likesRef.child(postIde).child(myUid).removeValue();
                              mProcessLike = false;
                          }
                          else{
                              postsRef.child(postIde).child("pLikes").setValue(""+ (pLikes+1));
                              likesRef.child(postIde).child(myUid).setValue("Liked");
                              mProcessLike = false;
                          }
                      }
                  }
                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {
                  }
              });
            }
        });

        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start postdetailactivity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Paylaş", Toast.LENGTH_SHORT).show();
            }
        });

        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);

                Toast.makeText(context, "Paylaş", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void setSaves(final MyHolder myHolder, final String postkey) {
//        savesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child(postkey).hasChild(myUid)){
//                    myHolder.save.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_saved_black,0,0,0);
//                }
//                else{
//                    myHolder.save.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_save,0,0,0);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void setLikes(final MyHolder holder, final String postKey) {

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(postKey).hasChild(myUid)){
                        holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked,0,0,0);
                       // holder.likeBtn.setText("Beğenildi");
                    }
                    else{
                        holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,0,0,0);
                        //holder.likeBtn.setText("Beğen");
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void isSaved(final String postid, final Button button){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).hasChild(myUid)){
                    ///eğer çalışmazsa burayı değiştirerek tekrar dene
                    button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_saved_black,0,0,0);
                    button.setTag("Kaydedildi");
                }else{
                    button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_saved,0,0,0);
                    button.setTag("Kaydet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, final String pId, final String pImage) {

        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        if (uid .equals(myUid)){

            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Sil");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Düzenle");
        }
        popupMenu.getMenu().add(Menu.NONE, 2,0,"Detaylar..");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id==0){
                    beginDelete(pId,pImage);

                }
                else if (id==1){

                    Intent intent = new Intent(context, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", pId);
                    context.startActivity(intent);
                }
                else if (id == 2){
                    //start postdetailactivity
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId", pId);
                    context.startActivity(intent);
                }
                return false;
            }
        });

        popupMenu.show();

    }

    private void beginDelete(String pId, String pImage) {
        if (pImage.equals("noImage")){

            deleteWithoutImage(pId);
        }
        else{
            deleteWithImage(pId, pImage);

        }
    }

    private void deleteWithImage(final String pId, String pImage) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Siliniyor...");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //image deleted, now delete database
                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                        fquery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();
                                }
                                Toast.makeText(context, "Silme işlemi tamamlandı", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed, cant go further
                        pd.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteWithoutImage(String pId) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Siliniyor...");


        //image deleted, now delete database
        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Silme işlemi tamamlandı", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        //views from row_post.xml
        ImageView uPictureIv, pImageIv;
        TextView uNameTv, pTimeTv, pTitleTv, pDescrTv, pLikesTv, pCommentsTv;
        ImageButton moreBtn;
        Button likeBtn, commentBtn, shareBtn, save;
        LinearLayout profileLayout;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            uPictureIv=itemView.findViewById(R.id.uPictureIv);
            pImageIv=itemView.findViewById(R.id.pImageIv);
            uNameTv=itemView.findViewById(R.id.uNameTv);
            pTimeTv=itemView.findViewById(R.id.pTimeTv);
            pTitleTv=itemView.findViewById(R.id.pTitleTv);
            pDescrTv=itemView.findViewById(R.id.pDescrTv);
            pLikesTv=itemView.findViewById(R.id.pLikesTv);
            save=itemView.findViewById(R.id.saveBtn);
            pCommentsTv=itemView.findViewById(R.id.pCommentsTv);
            moreBtn=itemView.findViewById(R.id.moreBtn);
            likeBtn=itemView.findViewById(R.id.likeBtn);
            commentBtn=itemView.findViewById(R.id.commentBtn);
            shareBtn=itemView.findViewById(R.id.shareBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);

        }
    }


}

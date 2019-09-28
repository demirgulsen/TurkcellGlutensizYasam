package com.example.benimprojem.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benimprojem.ChatActivity;
import com.example.benimprojem.R;
import com.example.benimprojem.ThereProfileActivity;
import com.example.benimprojem.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder> {

    Context context;
    List<ModelUser> userList;

    //constractor


    public AdapterUser(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //iflate layout(row_user.xml)

        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        //get data

        final String hisUID =userList.get(position).getUid();

        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        final String userEmail = userList.get(position).getEmail();

        //set data
        holder.mNameP.setText(userName);
        holder.mEmailP.setText(userEmail);
        try{
            Picasso.get().load(userImage).
                    placeholder(R.drawable.ic_default_img)
                    .into(holder.mAvatarP);


        }catch (Exception e){

        }
        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Profile", "Chat"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which ==0){
                            Intent intent = new Intent(context, ThereProfileActivity.class);
                            intent.putExtra("uid", hisUID);
                            context.startActivity(intent);

                        }
                        if (which ==1){
                            Intent intent= new Intent(context, ChatActivity.class);
                            intent.putExtra("hisUid", hisUID);                             // burası "hisUid" idi normalde
                            context.startActivity(intent);

                        }

                    }
                });
                builder.create().show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    //view holder class
    class  MyHolder extends RecyclerView.ViewHolder {

        ImageView mAvatarP;
        TextView mNameP, mEmailP;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //init view
            mAvatarP = itemView.findViewById(R.id.avatarP);
            mNameP = itemView.findViewById(R.id.nameP);
            mEmailP = itemView.findViewById(R.id.emailP);

        }
    }
}

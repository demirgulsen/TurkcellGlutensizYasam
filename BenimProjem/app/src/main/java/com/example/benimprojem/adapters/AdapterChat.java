package com.example.benimprojem.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benimprojem.R;
import com.example.benimprojem.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    String imageUrl;

    FirebaseUser fUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (i == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return  new MyHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return  new MyHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        //get data
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestamp();

        //convert time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dataTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        //set data
        holder.messageP.setText(message);
        holder.timeP.setText(dataTime);

        try{
            Picasso.get().load(imageUrl).into(holder.profilP);

        }catch (Exception e){

        }
        holder.messageLAyout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Sil");
                builder.setMessage("Silmek istediğinizden emin misininz");
                builder.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(position);
                    }
                });
                builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

            }
        });

        //seet seen/delivered status of message
        if (position == chatList.size()-1){
            if (chatList.get(position).isSeen()){
                holder.isSeen.setText("Görüldü");
            }
            else{
                holder.isSeen.setText("İletildi");
            }
        }
        else{
            holder.isSeen.setVisibility(View.GONE);
        }

    }

    private void deleteMessage(int i) {
        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String msgTimeStamp = chatList.get(i).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("sender").getValue().equals(myUID)){

                        //1.silme yöntemi
                        //ds.getRef().removeValue();

                        //2.silme yöntemi  hangisini istersen onu kullan
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", " Bu mesaj silindi..");
                        ds.getRef().updateChildren(hashMap);
                        //
                        Toast.makeText(context, "mesaj silindi", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, "Bu mesaj zaten silindi", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently signed in user
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views
        ImageView profilP;
        TextView messageP, timeP, isSeen;
        LinearLayout messageLAyout;



        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profilP = itemView.findViewById(R.id.profilP);
            messageP = itemView.findViewById(R.id.messageEt);
            timeP = itemView.findViewById(R.id.timeP);
            isSeen = itemView.findViewById(R.id.isSeen);
            messageLAyout = itemView.findViewById(R.id.messageLayout);
        }
    }

}

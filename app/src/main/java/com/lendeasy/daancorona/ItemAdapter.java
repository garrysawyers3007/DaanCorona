package com.lendeasy.daancorona;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.util.Strings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private View view;
    ArrayList<Item> list;
    Context context;
    public ItemAdapter(ArrayList<Item> list, Context context){
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.UiD.setText(list.get(position).getUid());
        holder.amount.setText(list.get(position).getAmount());

        SharedPreferences sharedPref=context.getSharedPreferences("User",MODE_PRIVATE);
        String token=sharedPref.getString("Token","");

        holder.thanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new Populate().execute(list.get(position).getUid(),token);
                Toast.makeText(context,"Sent!!!",Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,UiD,amount;
        Button thanks;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            name=itemView.findViewById(R.id.name);
            UiD=itemView.findViewById(R.id.UiD);
            amount=itemView.findViewById(R.id.amount);

            thanks=itemView.findViewById(R.id.thanks);
        }
    }

    class Populate extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {


            final OkHttpClient httpClient = new OkHttpClient();

            RequestBody formbody=new FormBody.Builder()
                    .addEncoded("donor_id",strings[0])
                    .build();

            Request request = new Request.Builder()
                    .url("http://daancorona.pythonanywhere.com/api/send_thanks/")
                    .addHeader("Authorization","JWT "+strings[1])
                    .post(formbody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                Log.d("Tag",response.body()+"");

                return "Done";

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            if(s!=null)
                Toast.makeText(context,"Sent!!!",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"Error",Toast.LENGTH_LONG).show();

        }
    }
}

package com.example.os150.otp;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by os150 on 2020-05-21.
 */

public class ProfileActivity extends ActivityGroup {

    private static int ALBUM_IMAGE_REQUEST = 1;

    int i = 0;
    Button profilereset;
    Button pwchange;
    Button withdrawal;
    Button logout;
    Button nicknamechange;
    TextView nicknametv;
    CircleImageView profileimage;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        profilereset = (Button)findViewById(R.id.profilechange);
        pwchange = (Button)findViewById(R.id.pwchange);
        withdrawal = (Button)findViewById(R.id.withdrawal);
        logout = (Button)findViewById(R.id.logout);
        nicknamechange = (Button)findViewById(R.id.nicknamechange);
        nicknametv = (TextView) findViewById(R.id.nicknametv);
        profileimage = (CircleImageView) findViewById(R.id.pimage);



        mDatabase.child("users").child("userInfo").child(user.getUid().toString()).child("nickname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                nicknametv.setText(dataSnapshot.getValue(String.class));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("알림","데이터 Load실패");

            }
        });

        mDatabase.child("users").child("userInfo").child(user.getUid().toString()).child("profileimage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               Log.v("알림","테스트 "+dataSnapshot.getValue(String.class));
               String profileimageUri = dataSnapshot.getValue(String.class);

               Uri imageuri = Uri.parse(profileimageUri);
               Log.v("알림","테스트 2"+imageuri);

               if((imageuri.toString().charAt(0)=='a')==true){
                   Log.v("알림","기본 이미지 "+imageuri.toString().charAt(0));
                   profileimage.setImageURI(imageuri);
               }
               else {
                   getRealPathFromURI(imageuri);
                   Log.v("알림", "절대 경로" + getRealPathFromURI(imageuri));
                   profileimage.setImageURI(Uri.parse(getRealPathFromURI(imageuri)));
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("알림","image load 실패");
            }
        });


                // 프로필 이미지 변경 클릭 시
                profilereset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder profileresetQ = new AlertDialog.Builder(ProfileActivity.this);
                        profileresetQ.setTitle("사진 업로드");
                        profileresetQ.setCancelable(false).setPositiveButton("카메라", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.v("알림", "camere 클릭");
                            }
                        });
                        profileresetQ.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.v("알림", "취소 클릭");
                                dialogInterface.cancel();
                            }
                        });
                        profileresetQ.setNegativeButton("앨범", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.v("알림", "앨범 클릭");
                                Intent albumintent = new Intent(Intent.ACTION_GET_CONTENT);
                                albumintent.setType("image/*");
                                startActivityForResult(albumintent.createChooser(albumintent, "앨범 가져오기"), ALBUM_IMAGE_REQUEST);
                            }
                        });
                        profileresetQ.show();
                    }
                });

        //비밀번호 변경 클릭 시
        pwchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText pwchangeed = new EditText(ProfileActivity.this);
                pwchangeed.getTransformationMethod();
                AlertDialog.Builder pwchangeQ = new AlertDialog.Builder(ProfileActivity.this);
                pwchangeQ.setTitle("비밀번호 변경");
                pwchangeQ.setView(pwchangeed);
                pwchangeQ.setMessage("새로운 비밀번호를 입력해 주세요.\n 변경 이후 되돌릴 수 없습니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        user.updatePassword(pwchangeed.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"변경 완료",Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                finish();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                        });
                    }
                });
                pwchangeQ.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소",Toast.LENGTH_SHORT).show();
                    }
                });
                pwchangeQ.show();

            }
        });

        //로그아웃 버튼 클릭 시
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));

            }
        });

        //회원탈퇴 버튼 클릭 시
        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder withdrawalQ = new AlertDialog.Builder(ProfileActivity.this);
                    withdrawalQ.setMessage("정말 계정을 삭제하시겠습니까 ? ").setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.v("알림","계정 삭제 진행 완료");
                                    Toast.makeText(getApplicationContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));


                                }
                            });
                            mDatabase.child("users").child("userInfo").child(user.getUid()).setValue(null);

                        }
                    });
                    withdrawalQ.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
                        }
                    });
                    withdrawalQ.show();
                }
                catch(Exception e){
                    Log.e("에러", Log.getStackTraceString(e));

                }
            }
        });

        nicknamechange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText nchange = new EditText(ProfileActivity.this);
                nchange.getTransformationMethod();

                AlertDialog.Builder nicknameQ = new AlertDialog.Builder(ProfileActivity.this);
                nicknameQ.setTitle("별명");
                nicknameQ.setView(nchange);
                nicknameQ.setMessage("새로운 별명을 입력해주세요.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatabase.child("users").child("userInfo").child(user.getUid().toString()).child("nickname").setValue(nchange.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"별명 변경 완료", Toast.LENGTH_SHORT).show();
//                                finish();
//                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                        });

                    }
                });
                nicknameQ.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소",Toast.LENGTH_SHORT).show();
                    }
                });
                nicknameQ.show();


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try{
            if(requestCode==ALBUM_IMAGE_REQUEST&&resultCode==RESULT_OK&&data!=null){

                imageUri = data.getData();
              //  File filePath = new File(imageUri.getPath());
                //Log.v("알림","이미지 경로 : "+filePath);
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("변경 중..");
                progressDialog.show();

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                Log.v("알림","bitmap 이미지 경로 : "+bitmap);

                profileimage.setImageBitmap(bitmap);
                mDatabase.child("users").child("userInfo").child(user.getUid().toString()).child("profileimage").setValue(imageUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"프로필 데이터베이스 변경 성공", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"프로필 데이터베이스 변경 실패",Toast.LENGTH_SHORT).show();
                    }
                });

                if(imageUri!=null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH" + i++);
                    Date date = new Date();
                    String filename = formatter.format(date) + ".png";
                    StorageReference srf = storage.getReferenceFromUrl("gs://otpdata-edb66.appspot.com").child("profileimages/" + filename);
                    srf.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Log.v("알림", "업로드 완료");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Log.v("알림", "업로드 실패");
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage(((int) progress) + "%...");
                        }
                    });


                }
            }else{
                Toast.makeText(getApplicationContext(),"취소",Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI (Uri contentUri){
       if(contentUri.getPath().startsWith("/storage")){
           return contentUri.getPath();
       }
       String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
       String[] columns = {MediaStore.Files.FileColumns.DATA};
       String selection = MediaStore.Files.FileColumns._ID+"="+id;
       Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"),columns,selection,null,null);
       try{
        int columnIndex = cursor.getColumnIndex(columns[0]);
        if(cursor.moveToFirst()){
            return cursor.getString(columnIndex);
        }
       }finally{
           cursor.close();
       }
       return null;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}

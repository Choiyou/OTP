package com.example.os150.otp;


import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by os150 on 2020-05-21.
 */

public class ProfileActivity extends ActivityGroup {

    private static int ALBUM_IMAGE_REQUEST = 1;
    private static int CAMERA_IMAGE_REQUEST = 2;

    TextView pnickname;
    CircleImageView pimageview;
    Button withdrawalbtn;
    Button logoutbtn;
    Button changeprofile;
    Button changenickname;
    Button changepw;

    int count;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    Date date = new Date();

    List<ChatModel> chatroom = new ArrayList<>();


    private File imagefile;
    Uri albumuri;
    Uri cameraUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pnickname = (TextView) findViewById(R.id.nicknametv);
        pimageview = (CircleImageView) findViewById(R.id.pimage);
        withdrawalbtn = (Button) findViewById(R.id.withdrawal);
        logoutbtn = (Button) findViewById(R.id.logout);
        changeprofile = (Button) findViewById(R.id.profilechange);
        changenickname = (Button) findViewById(R.id.nicknamechange);
        changepw = (Button) findViewById(R.id.pwchange);
        if (count == 1000) {
            count = 0;
        }

        try {
            // 별명 불러오기
            mDatabase.child("userInfo").child(user.getUid()).child("nickname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pnickname.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v("알림", "데이터 로드 실패");
                }
            });
            mDatabase.child("myfriendlist").orderByChild(user.getUid()).equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String name = snapshot.getValue(String.class);
                        Log.e("알림", "오류 : " + name);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //프로필 이미지 불러오기
            mDatabase.child("userInfo").child(user.getUid()).child("profileimage").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String pprofileimage = dataSnapshot.getValue(String.class);
                    Log.e("알림", "받아온 값 : " + pprofileimage);
                    try {
                        if ((pprofileimage.charAt(0) == 'a')) {
                            return;
                        } else {
                            Glide.with(getApplicationContext()).load(pprofileimage).into(pimageview);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.v("알림", "데이터 로드 실패");
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }



        //회원탈퇴 버튼 클릭 이벤트
        withdrawalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder withdrawablQ = new AlertDialog.Builder(ProfileActivity.this);
                withdrawablQ.setMessage("정말 계정을 삭제하시겠습니까 ? ").setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {

                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                chatroom.clear();
                                Log.v("알림", "회원 탈퇴 진행 완료");
                                //회원 탈퇴 시 채팅방 UserInfo 삭제, 내 친구 삭제, 유저 관심 상품 삭제, 내 게시글 삭제
                                mDatabase.child("chatuserInfo").child(user.getUid()).removeValue();
                                mDatabase.child("myfriendlist").child(user.getUid()).removeValue();
                                mDatabase.child("myfriendlist").orderByChild(user.getUid()).equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            snapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                mDatabase.child("chatroom").orderByChild("users/" + user.getUid()).equalTo(true).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            snapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                mDatabase.child("UserLikeCategory").child(user.getUid()).removeValue();
                                mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Useruid").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                            snapshot2.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                                mDatabase.child("userInfo").child(user.getUid()).removeValue();
                                mAuth.signOut();
                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }


                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.v("알림", "회원 탈퇴 실패");

                            }
                        });

                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("알림", "회원 탈퇴 진행 취소");
                    }
                });
                withdrawablQ.show();

            }
        });

        //로그아웃 버튼 클릭 이벤트
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }
        });
        //프로필 변경 클릭 시
        changeprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //AlertDialog생성
                AlertDialog.Builder profileresetQ = new AlertDialog.Builder(ProfileActivity.this);
                profileresetQ.setTitle("프로필 이미지 변경");
                profileresetQ.setCancelable(false).setPositiveButton("카메라", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("알림", "카메라 항목 클릭");
                        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // cameraintent를 사용할 수 있는 Activity가 있는지 확인
                        if (cameraintent.resolveActivity(getPackageManager()) != null) {
                            try {
                                imagefile = CameraImage();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //파일이 없으 ㄹ경우
                            if (imagefile != null) {
                                // imagefile을 열어 권한을 받아와 파일 공유
                                cameraUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.os150.otp.fileprovider", imagefile);
                                cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);// cameraUri 로 Uri 지정 데이터 전달
                                startActivityForResult(cameraintent, CAMERA_IMAGE_REQUEST);
                            }
                        }

                    }
                }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //AlertDialog 취소 버튼 클릭 시
                        Log.v("알림", "취소 항목 클릭");
                        dialogInterface.cancel(); //다이어 로그 닫기
                    }
                }).setNegativeButton("앨범", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //AlertDialog 앨범 항목 클릭 시
                        Log.v("알림", "앨범 항목 클릭");
                        Intent albumintent = new Intent(Intent.ACTION_PICK);
                        albumintent.setType("image/*");
                        albumintent.setAction(Intent.ACTION_GET_CONTENT); // 이미지 경로값 넘기기
                        startActivityForResult(albumintent, ALBUM_IMAGE_REQUEST);
                    }
                });
                profileresetQ.show();

            }
        });

        //별명 버튼 클릭 시 작동
        changenickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText nchange = new EditText(ProfileActivity.this);
                nchange.getTransformationMethod();
                nchange.setSingleLine(); //singleline처리

                final AlertDialog.Builder nicknamechangeQ = new AlertDialog.Builder(ProfileActivity.this);
                nicknamechangeQ.setTitle("별명");
                nicknamechangeQ.setView(nchange);

                nicknamechangeQ.setMessage("새로운 별명을 입력해주세요.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (nchange.getText().length() == 0) {
                            dialogInterface.dismiss();
                        } else {
                            //Database 추가
                            mDatabase.child("chatuserInfo").child(user.getUid().toString()).child("nickname").setValue(nchange.getText().toString());
                            mDatabase.child("userInfo").child(user.getUid().toString()).child("nickname").setValue(nchange.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.v("알림", "별명 변경 완료");
                                }
                            });
                        }
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("알림", "별명 변경 취소");
                    }
                });
                nicknamechangeQ.show();

            }
        });
        //비밀번호 재설정 클릭 작동
        changepw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText changepassword = new EditText(ProfileActivity.this);

                changepassword.setSingleLine();
                changepassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); //텍스트 비밀번호 표시
                changepassword.getTransformationMethod();
                final AlertDialog.Builder changepwQ = new AlertDialog.Builder(ProfileActivity.this);
                changepwQ.setTitle("비밀번호 변경");
                changepwQ.setView(changepassword);
                changepwQ.setMessage("새로운 비밀번호를 입력해주세요.\n 변경 이후엔 되돌릴 수 없습니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (changepassword.getText().length() == 0) {
                            dialogInterface.dismiss();
                        } else {
                            //User 비밀 번호 변경
                            user.updatePassword(changepassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.v("알림", "비밀번호 변경 완료");
                                    mAuth.signOut(); // 로그인한 유저 로그아웃
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }
                            });
                        }
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("알림", "비밀번호 변경 취소");
                    }
                });
                changepwQ.show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        //requestCode가 CAMERA_IMAGE_REQUEST값과 동일할 경우
        if (requestCode == CAMERA_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {

                Log.v("알림", "올라간 파일 이름 : " + cameraUri.getPath());
                String camerauri = cameraUri.getPath().toString().substring(37, cameraUri.getPath().toString().length());
                Log.v("알림", "자른 경로 : " + camerauri);
                //Stroage에 올리기 위한 작업
                StorageReference SRF = storage.getReferenceFromUrl("gs://otpdata-edb66.appspot.com/").child("userprofileImage/" + camerauri);

                //올린파일 이름이 있을 경우
                if (camerauri != null) {
                    progressDialog.setTitle("업로드");
                    progressDialog.show();
                    //Storage에 파일 업로드
                    SRF.putFile(cameraUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Uri downloadcameraUrl = taskSnapshot.getDownloadUrl(); // 저장된 이미지 다운로드
                            Glide.with(getApplicationContext()).load(downloadcameraUrl).into(pimageview);
                            Log.v("알림", "다운로드 경로 : " + downloadcameraUrl);
                            //DataBase 정보 업데이트
                            mDatabase.child("chatuserInfo").child(user.getUid().toString()).child("profileimage").setValue(downloadcameraUrl.toString());
                            mDatabase.child("userInfo").child(user.getUid().toString()).child("profileimage").setValue(downloadcameraUrl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "데이터베이스 변경 실패", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v("알림", "데이터베이스 변경 실패");
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("업로드 : " + ((int) progress) + "%");
                        }
                    });
                }
            }
        }

        // RequestCode 값과 ALBUM_IMAGE_REQUEST 값과 동일할 경우
        if (requestCode == ALBUM_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                try {
                    albumuri = data.getData(); //Uri 가져오기
                    String albumImage = formatter.format(date);
                    Log.v("알림", "AlbumImage file : " + albumImage);
                    StorageReference srf = storage.getReferenceFromUrl("gs://otpdata-edb66.appspot.com/").child("userprofileImage/" + albumImage + "_" + count++ + ".png");
                    if (albumuri != null) {
                        progressDialog.setTitle("프로필 업로드");
                        progressDialog.show();
                        //Storage에 파일 업로드
                        srf.putFile(albumuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Uri downloadalbumUri = taskSnapshot.getDownloadUrl(); // 이미지 다운로드
                                //pimageview.setImageURI(downloadUri);

                                Glide.with(getApplicationContext()).load(downloadalbumUri).into(pimageview); //CircleImageVIew에 띄우기
                                Log.v("알림", "다운로드 이미지 : " + downloadalbumUri);

                                //Database 업데이트
                                mDatabase.child("chatuserInfo").child(user.getUid().toString()).child("profileimage").setValue(downloadalbumUri.toString());
                                mDatabase.child("userInfo").child(user.getUid().toString()).child("profileimage").setValue(downloadalbumUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Log.v("알림", "데이터베이스 변경 성공");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.v("알림", "데이터베이스 변경 실패");
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.v("알림", "데이터베이스 변경 실패");
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage("업로드 : " + ((int) progress) + "%");
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    //카메라가 찍은 이미지 저장 파일
    private File CameraImage() throws IOException {
        //파일 이름
        String imagefilename = "cameraImage_" + String.valueOf(System.currentTimeMillis()) + ".png";
        //이미지가 저장될 폴더 명
        File StorageFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/" + imagefilename);
//        if(!StorageFile.exists())
//            StorageFile.mkdirs();
        return StorageFile;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), SecondMainActivity.class));
        finish();
    }
}



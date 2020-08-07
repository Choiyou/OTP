package com.example.os150.otp;


import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by os150 on 2020-05-21.
 */

public class ProfileActivity extends ActivityGroup {

    private static int ALBUM_IMAGE_REQUEST = 1;
    private static int CAMERA_IMAGE_REQUEST = 2;

    Uri albumuri;
    TextView pnickname;
    CircleImageView pimageview;
    Button withdrawalbtn;
    Button logoutbtn;
    Button changeprofile;
    Button changenickname;
    Button changepw;

    int count = 0;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    Date date = new Date();

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

            //프로필 이미지 불러오기
            mDatabase.child("userInfo").child(user.getUid()).child("profileimage").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String pprofileimage = dataSnapshot.getValue(String.class);
                    if ((pprofileimage.charAt(0) == 'a') == true) {
                        return;
                    } else {
                        Glide.with(getApplicationContext()).load(pprofileimage).into(pimageview);
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
        if (count == 1000) {
            count = 0;
        }


        //회원탈퇴 버튼 클릭 이벤트
        withdrawalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder withdrawablQ = new AlertDialog.Builder(ProfileActivity.this);
                withdrawablQ.setMessage("정말 계정을 삭제하시겠습니까 ? ").setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatabase.child("userInfo").child(user.getUid()).setValue(null);
                        mDatabase.child("chatuserInfo").child(user.getUid().toString()).setValue(null);
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.v("알림", "회원 탈퇴 진행 완료");

                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));

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
        changeprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder profileresetQ = new AlertDialog.Builder(ProfileActivity.this);
                profileresetQ.setTitle("프로필 이미지 변경");
                profileresetQ.setCancelable(false).setPositiveButton("카메라", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("알림", "카메라 항목 클릭");


                    }
                }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("알림", "취소 항목 클릭");
                        dialogInterface.cancel();
                    }
                }).setNegativeButton("앨범", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("알림", "앨범 항목 클릭");
                        Intent albumintent = new Intent(Intent.ACTION_PICK);
                        albumintent.setType("image/*");
                        albumintent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(albumintent, ALBUM_IMAGE_REQUEST);
                    }
                });
                profileresetQ.show();

            }
        });

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
        //비밀번호 재설정
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
                            user.updatePassword(changepassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.v("알림", "비밀번호 변경 완료");

                                    mAuth.signOut();
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
        if (requestCode == ALBUM_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                try {
//                    InputStream in = getContentResolver().openInputStream(data.getData());
//                    Bitmap bitimage = BitmapFactory.decodeStream(in);
//                    in.close();
//                    pimageview.setImageBitmap(bitimage);
                    albumuri = data.getData();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    String albumImage = formatter.format(date);
                    Log.v("알림", "AlbumImage file : " + albumImage);
                    StorageReference srf = storage.getReferenceFromUrl("gs://otpdata-edb66.appspot.com/").child("userprofileImage/" + albumImage + "_" + count++ + ".png");
                    if (albumuri != null) {
                        srf.putFile(albumuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                //pimageview.setImageURI(downloadUri);
                                Glide.with(getApplicationContext()).load(downloadUri).into(pimageview);
                                Log.v("알림", "다운로드 이미지 : " + downloadUri);

                                mDatabase.child("chatuserInfo").child(user.getUid().toString()).child("profileimage").setValue(downloadUri.toString());

                                mDatabase.child("userInfo").child(user.getUid().toString()).child("profileimage").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "데이터베이스 변경 성공", Toast.LENGTH_SHORT).show();

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
                                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), SecondMainActivity.class));
        finish();
    }
}


//package com.example.os150.otp;
//
//
//        import android.app.ActivityGroup;
//        import android.app.AlertDialog;
//        import android.app.ProgressDialog;
//        import android.content.DialogInterface;
//        import android.content.Intent;
//        import android.database.Cursor;
//        import android.net.Uri;
//        import android.os.Bundle;
//        import android.os.Environment;
//        import android.provider.DocumentsContract;
//        import android.provider.MediaStore;
//        import android.support.annotation.NonNull;
//        import android.support.v4.content.FileProvider;
//        import android.text.InputType;
//        import android.util.Log;
//        import android.view.View;
//        import android.widget.Button;
//
//        import android.widget.EditText;
//        import android.widget.TextView;
//        import android.widget.Toast;
//
//        import com.google.android.gms.tasks.OnCompleteListener;
//        import com.google.android.gms.tasks.OnFailureListener;
//        import com.google.android.gms.tasks.OnSuccessListener;
//        import com.google.android.gms.tasks.Task;
//        import com.google.firebase.auth.FirebaseAuth;
//        import com.google.firebase.auth.FirebaseUser;
//        import com.google.firebase.database.DataSnapshot;
//        import com.google.firebase.database.DatabaseError;
//        import com.google.firebase.database.DatabaseReference;
//        import com.google.firebase.database.FirebaseDatabase;
//        import com.google.firebase.database.ValueEventListener;
//        import com.google.firebase.storage.FirebaseStorage;
//        import com.google.firebase.storage.OnProgressListener;
//        import com.google.firebase.storage.StorageReference;
//        import com.google.firebase.storage.UploadTask;
//
//        import java.io.File;
//        import java.io.IOException;
//        import java.text.SimpleDateFormat;
//        import java.util.Date;
//        import java.util.Locale;
//
//        import de.hdodenhof.circleimageview.CircleImageView;
//
///**
// * Created by os150 on 2020-05-21.
// */

//public class ProfileActivity extends ActivityGroup {
//
//    private static int ALBUM_IMAGE_REQUEST = 1;
//    private static int CAMERA_IMAGE_REQUEST = 2;
//
//
//    int count = 0;
//
//    Uri cameraUri;
//
//    Uri profileimageu;
//    TextView pnickname;
//    CircleImageView pimageview;
//    Button withdrawalbtn;
//    Button logoutbtn;
//    Button changeprofile;
//    Button changenickname;
//    Button changepw;
//
//
//    FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//    FirebaseUser user = mAuth.getCurrentUser();
//
//    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
//    Date date = new Date();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
//
//        pnickname = (TextView) findViewById(R.id.nicknametv);
//        pimageview = (CircleImageView) findViewById(R.id.pimage);
//        withdrawalbtn = (Button) findViewById(R.id.withdrawal);
//        logoutbtn = (Button) findViewById(R.id.logout);
//        changeprofile = (Button) findViewById(R.id.profilechange);
//        changenickname = (Button) findViewById(R.id.nicknamechange);
//        changepw = (Button) findViewById(R.id.pwchange);
//
//
//        try {
//            // 별명 불러오기
//            mDatabase.child("userInfo").child(user.getUid()).child("nickname").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    pnickname.setText(dataSnapshot.getValue(String.class));
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.v("알림", "데이터 로드 실패");
//                }
//            });
//
//            //프로필 이미지 불러오기
//            mDatabase.child("userInfo").child(user.getUid()).child("profileimage").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    String pprofileimage = dataSnapshot.getValue(String.class);
//                    if (pprofileimage != null) {
//                        if ((pprofileimage.charAt(0) == 'a') == true) {
//                            return;
//                        } else {
//                            profileimageu = Uri.parse(pprofileimage); //프로필 imageUri문자열 값 Uri로 변경
//                            pimageview.setImageURI(Uri.parse(getRealPathFromUri(profileimageu)));
//                        }
//                    } else {
//                        Log.v("알림", "null이 아님");
//                    }
//                    Log.v("알림", "데이터베이스에서 불러온 값 : " + pprofileimage);
//                    Log.v("알림", "불러온 값 Uri 변환 : " + profileimageu);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                    Log.v("알림", "데이터 로드 실패");
//                }
//            });
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//
//
//        //회원탈퇴 버튼 클릭 이벤트
//        withdrawalbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder withdrawablQ = new AlertDialog.Builder(ProfileActivity.this);
//                withdrawablQ.setMessage("정말 계정을 삭제하시겠습니까 ? ").setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        mDatabase.child("userInfo").child(user.getUid()).setValue(null);
//                        mDatabase.child("chatuserInfo").child(user.getUid().toString()).setValue(null);
//                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Log.v("알림", "회원 탈퇴 진행 완료");
//
//                                finish();
//                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//
//                            }
//
//
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.v("알림", "회원 탈퇴 실패");
//
//                            }
//                        });
//
//
//                    }
//                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Log.v("알림", "회원 탈퇴 진행 취소");
//                    }
//                });
//                withdrawablQ.show();
//
//            }
//        });
//
//        //로그아웃 버튼 클릭 이벤트
//        logoutbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                mAuth.signOut();
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                finish();
//
//            }
//        });
//        changeprofile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder profileresetQ = new AlertDialog.Builder(ProfileActivity.this);
//                profileresetQ.setTitle("프로필 이미지 변경");
//                profileresetQ.setCancelable(false).setPositiveButton("카메라", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Log.v("알림", "카메라 항목 클릭");
//                        Intent camerai = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        if (camerai.resolveActivity(getPackageManager()) != null) {
//                            File photofile = null;
//                            try {
//                                photofile = CameraImage();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            if (photofile != null) {
//                                cameraUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.os150.otp.fileprovider", photofile);
//                                camerai.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
//                                startActivityForResult(camerai, CAMERA_IMAGE_REQUEST);
//                            }
//                        }
//
//                    }
//                }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Log.v("알림", "취소 항목 클릭");
//                        dialogInterface.cancel();
//                    }
//                }).setNegativeButton("앨범", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Log.v("알림", "앨범 항목 클릭");
//                        Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                        albumIntent.setType("image/*");
//                        startActivityForResult(albumIntent.createChooser(albumIntent, "앨범 가져오기"), ALBUM_IMAGE_REQUEST);
//                    }
//                });
//                profileresetQ.show();
//
//            }
//        });
//
//        changenickname.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final EditText nchange = new EditText(ProfileActivity.this);
//                nchange.getTransformationMethod();
//                nchange.setSingleLine(); //singleline처리
//
//                final AlertDialog.Builder nicknamechangeQ = new AlertDialog.Builder(ProfileActivity.this);
//                nicknamechangeQ.setTitle("별명");
//                nicknamechangeQ.setView(nchange);
//
//                nicknamechangeQ.setMessage("새로운 별명을 입력해주세요.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if (nchange.getText().length() == 0) {
//                            dialogInterface.dismiss();
//                        } else {
//                            mDatabase.child("chatuserInfo").child(user.getUid().toString()).child("nickname").setValue(nchange.getText().toString());
//                            mDatabase.child("userInfo").child(user.getUid().toString()).child("nickname").setValue(nchange.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Log.v("알림", "별명 변경 완료");
//                                }
//                            });
//                        }
//                    }
//                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Log.v("알림", "별명 변경 취소");
//                    }
//                });
//                nicknamechangeQ.show();
//
//            }
//        });
//        //비밀번호 재설정
//        changepw.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final EditText changepassword = new EditText(ProfileActivity.this);
//                changepassword.setSingleLine();
//                changepassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); //텍스트 비밀번호 표시
//
//                changepassword.getTransformationMethod();
//
//                final AlertDialog.Builder changepwQ = new AlertDialog.Builder(ProfileActivity.this);
//                changepwQ.setTitle("비밀번호 변경");
//                changepwQ.setView(changepassword);
//                changepwQ.setMessage("새로운 비밀번호를 입력해주세요.\n 변경 이후엔 되돌릴 수 없습니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if (changepassword.getText().length() == 0) {
//                            dialogInterface.dismiss();
//                        } else {
//                            user.updatePassword(changepassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Log.v("알림", "비밀번호 변경 완료");
//
//                                    mAuth.signOut();
//                                    finish();
//                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
//
//                                }
//                            });
//                        }
//                    }
//                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Log.v("알림", "비밀번호 변경 취소");
//                    }
//                });
//                changepwQ.show();
//            }
//        });
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        try {
//            if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
//                String camerauri = cameraUri.getPath().toString().substring(5, cameraUri.getPath().toString().length());
//                Uri cameraURI = Uri.parse(camerauri);
//                pimageview.setImageURI(cameraURI);
//                mDatabase.child("chatuserInfo").child(user.getUid().toString()).child("profileimage").setValue(cameraURI.toString());
//                mDatabase.child("userInfo").child(user.getUid().toString()).child("profileimage").setValue(cameraURI.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getApplicationContext(), "프로필 이미지 변경 성공", Toast.LENGTH_SHORT).show();
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(), "프로필 이미지 변경 실패", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//            }
//
//            if (requestCode == ALBUM_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
//                profileimageu = data.getData();
//                final ProgressDialog albumP = new ProgressDialog(this);
//                albumP.setTitle("변경중 ...");
//                albumP.show();
//
//                Uri albumimage;
//
//                //Bitmap bitmapalbum = MediaStore.Images.Media.getBitmap(getContentResolver(), profileimageu);
//                //   Log.v("알림", "image Bitmap 경로 : " + bitmapalbum);
//                pimageview.setImageURI(profileimageu);
//                //pimageview.setImageBitmap(bitmapalbum);
//                mDatabase.child("chatuserInfo").child(user.getUid().toString()).child("profileimage").setValue(profileimageu.toString());
//
//                mDatabase.child("userInfo").child(user.getUid().toString()).child("profileimage").setValue(profileimageu.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getApplicationContext(), "데이터베이스 변경 성공", Toast.LENGTH_SHORT).show();
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(), "데이터베이스 변경 실패", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//                if (profileimageu != null) {
//
//                    final FirebaseStorage storage = FirebaseStorage.getInstance();
//                    final String filename = formatter.format(date);
//
//                    StorageReference srf = storage.getReferenceFromUrl("gs://otpdata-edb66.appspot.com").child("profileimages/" + filename + count++);
//                    if (count >= 100000) {
//                        count = 0;
//                    }
//                    srf.putFile(profileimageu).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            albumP.dismiss();
//                            Log.v("알림", "업로드 위치 : " + storage.getReferenceFromUrl("gs://otpdata-edb66.appspot.com").child("profileimages/" + filename));
//                            Log.v("알림", "이미지 업로드 완료");
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            albumP.dismiss();
//                            Log.v("알림", "이미지 업로드 실패");
//                        }
//                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                            albumP.setMessage(((int) progress) + " % ...");
//
//                        }
//                    });
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public String getRealPathFromUri(Uri contentUri) {
//        if (contentUri.getPath().startsWith("/storage")) {
//            return contentUri.getPath();
//        }
//        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
//        String[] columns = {MediaStore.Files.FileColumns.DATA};
//        String selection = MediaStore.Files.FileColumns._ID + "=" + id;
//        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
//        try {
//            int columnIndex = cursor.getColumnIndex(columns[0]);
//            if (cursor.moveToFirst()) {
//                return cursor.getString(columnIndex);
//            }
//        } finally {
//            cursor.close();
//        }
//        return null;
//    }
//
//    private File CameraImage() throws IOException {
//        String imagefilename = "cameraimage" + String.valueOf(System.currentTimeMillis()) + ".jpeg";
//        File storage = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/" + imagefilename);
//        return storage;
//    }
//
//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(getApplicationContext(), SecondMainActivity.class));
//        finish();
//    }
//}

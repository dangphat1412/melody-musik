package com.example.melodymusikserverside;

import static com.example.melodymusikserverside.Models.Constants.DATABASE_PATH_CATEGORIES;
import static com.example.melodymusikserverside.Models.Constants.DATABASE_PATH_SONGS;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.melodymusikserverside.Adapter.CatogoriesSpinnerAdapter;
import com.example.melodymusikserverside.Adapter.MyArtistAdapter;
import com.example.melodymusikserverside.Models.Artist;
import com.example.melodymusikserverside.Models.Catogories;
import com.example.melodymusikserverside.Models.Constants;
import com.example.melodymusikserverside.Models.Song;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AddSongFragment extends Fragment {

    private EditText ed_song_name;
    private ImageButton btn_add_artist;
    private ImageButton btn_remove_artist;
    private Spinner spinner_catogories;
    private CatogoriesSpinnerAdapter spinnerAdapter;
    private LinearLayout ll_select_song_image;
    private LinearLayout ll_select_banner_image;
    private Button btn_add_new;
    private Button btn_choose_song;
    private TextView tv_choose_song;
    private ImageView iv_select_song_image;
    private ImageView iv_select_banner_image;
    private LinkedHashMap<String, Artist> artistList;
    private EditText ed_artist_name;
    private Uri imageFilePath;
    private Uri bannerFilePath;
    private Uri songFilePath;
    private static final int PICK_SONG_IMAGE_REQUEST = 234;
    private static final int PICK_SONG_BANNER_REQUEST = 235;
    private static final int PICK_SONG_REQUEST = 236;
    private DatabaseReference songDatabaseRef;
    private StorageReference storageReference;
    private DatabaseReference categoryReference;
    private List<Catogories> catogoriesList;
    String imageUrl;
    String bannerUrl;
    String songUrl;
    String category;


    private MyArtistAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_song, container, false);
        artistList = new LinkedHashMap<>();
        songDatabaseRef = FirebaseDatabase.getInstance().getReference().child(DATABASE_PATH_SONGS);
        categoryReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        ed_song_name = rootView.findViewById(R.id.ed_song_name);
        ed_artist_name = rootView.findViewById(R.id.ed_artist_name);
        iv_select_song_image = rootView.findViewById(R.id.iv_select_song_image);
        iv_select_banner_image = rootView.findViewById(R.id.iv_select_banner_image);
        tv_choose_song = rootView.findViewById(R.id.tv_choose_song);

        //spinner catogories
        spinner_catogories = rootView.findViewById(R.id.spinner_catogories);

        catogoriesList = new ArrayList<>();
        categoryReference.child(DATABASE_PATH_CATEGORIES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        catogoriesList.add(s.getValue(Catogories.class));
                    }
                    spinnerAdapter = new CatogoriesSpinnerAdapter(getActivity().getApplicationContext(), catogoriesList);
                    spinner_catogories.setAdapter(spinnerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinner_catogories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_choose_song = rootView.findViewById(R.id.btn_choose_song);
        btn_choose_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Song"), PICK_SONG_REQUEST);
            }
        });

        ll_select_song_image = rootView.findViewById(R.id.ll_select_song_image);
        ll_select_song_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_SONG_IMAGE_REQUEST);
            }
        });

        ll_select_banner_image = rootView.findViewById(R.id.ll_select_banner_image);
        ll_select_banner_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_SONG_BANNER_REQUEST);
            }
        });

        btn_add_artist = rootView.findViewById(R.id.btn_select_artist);
        btn_add_artist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog(rootView);
            }
        });

        btn_add_new = rootView.findViewById(R.id.btn_add_new);
        btn_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_song_name.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter song name!", Toast.LENGTH_SHORT).show();
                } else if (imageFilePath == null) {
                    Toast.makeText(getActivity(), "Select song image!", Toast.LENGTH_SHORT).show();
                } else if (bannerFilePath == null) {
                    Toast.makeText(getActivity(), "Select song banner!", Toast.LENGTH_SHORT).show();
                } else if (artistList.isEmpty()) {
                    Toast.makeText(getActivity(), "Select artist!", Toast.LENGTH_SHORT).show();
                } else if (songFilePath == null) {
                    Toast.makeText(getActivity(), "Select song!", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        btn_remove_artist = rootView.findViewById(R.id.btn_remove_artist);
        btn_remove_artist.setVisibility(View.GONE);
        btn_remove_artist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_artist_name.getText().clear();
                artistList.clear();
                btn_remove_artist.setVisibility(View.GONE);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_SONG_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageFilePath = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageFilePath);
                iv_select_song_image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_SONG_BANNER_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            bannerFilePath = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), bannerFilePath);
                iv_select_banner_image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_SONG_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            songFilePath = data.getData();
            String fileNames = getFileName(songFilePath);
            tv_choose_song.setText(fileNames);
        }
    }

    private void showBottomSheetDialog(View rootView) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                getContext(), R.style.BottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(
                        R.layout.bottom_sheet_choose_artist,
                        (LinearLayout) rootView.findViewById(R.id.bottomSheetContainer)
                );


        EditText mSearchField;
        ImageButton mSearchBtn;
        RecyclerView rvSingerBottomSheet;

        mSearchField = (EditText) bottomSheetView.findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) bottomSheetView.findViewById(R.id.search_btn);
        rvSingerBottomSheet = bottomSheetView.findViewById(R.id.rvSingerBottomSheet);
        rvSingerBottomSheet.setLayoutManager(new LinearLayoutManager(getContext()));

        //Search Artist in bottomSheet of add_song_fragment
        FirebaseRecyclerOptions<Artist> options =
                new FirebaseRecyclerOptions.Builder<Artist>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("singers"), Artist.class)
                        .build();
        //

        adapter = new MyArtistAdapter(options);
        adapter.startListening();
        rvSingerBottomSheet.setAdapter(adapter);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearchField.getText().toString().trim();
                FirebaseRecyclerOptions<Artist> options = new FirebaseRecyclerOptions.Builder<Artist>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("singers")
                                .orderByChild("name").startAt(searchText)
                                .endAt(searchText + "\uf8ff"), Artist.class)
                        .build();
                adapter = new MyArtistAdapter(options);
                adapter.startListening();
                rvSingerBottomSheet.setAdapter(adapter);
                //
            }
        });

        //Display HORIZONTAL
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvSingerBottomSheet.setLayoutManager(linearLayoutManager);
        rvSingerBottomSheet.addItemDecoration(new DividerItemDecoration(rvSingerBottomSheet.getContext(), DividerItemDecoration.VERTICAL));
        ViewGroup.LayoutParams params = rvSingerBottomSheet.getLayoutParams();
        params.height = 2000;
        rvSingerBottomSheet.setLayoutParams(params);


        rvSingerBottomSheet.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rvSingerBottomSheet, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Artist artist = adapter.getItem(position);
                String key = adapter.getRef(position).getKey();
                if(artistList.containsKey(key)) {
                    Toast.makeText(getContext(), artist.getName() + " has already selected!", Toast.LENGTH_SHORT).show();
                    return;
                }
                artistList.put(key, artist);
                ed_artist_name.setText(ed_artist_name.getText().toString().trim() + (ed_artist_name.getText().toString().isEmpty() ? "" : ", ") + artist.getName());
                Toast.makeText(getContext(), "Selected " + artist.getName(), Toast.LENGTH_SHORT).show();
                if (!artistList.isEmpty()) {
                    btn_remove_artist.setVisibility(View.VISIBLE);
                }
                bottomSheetDialog.dismiss();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadFile() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference sImageRef = storageReference.child(Constants.STORAGE_PATH_SONG_IMAGES + System.currentTimeMillis() + "." + getFileExtension(imageFilePath));
        sImageRef.putFile(imageFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri imageUri) {
                        imageUrl = imageUri.toString();
                        StorageReference sBannerRef = storageReference.child(Constants.STORAGE_PATH_BANNERS + System.currentTimeMillis() + "." + getFileExtension(bannerFilePath));
                        sBannerRef.putFile(bannerFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                sBannerRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri bannerUri) {
                                        bannerUrl = bannerUri.toString();

                                        StorageReference sSongRef = storageReference.child(Constants.STORAGE_PATH_SONGS + System.currentTimeMillis() + "." + getFileExtension(songFilePath));
                                        sSongRef.putFile(songFilePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }
                                                return sSongRef.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                songUrl = task.getResult().toString();
                                                String songId = songDatabaseRef.push().getKey();
                                                Song song = new Song(songId, ed_song_name.getText().toString().trim(), artistList, songUrl, imageUrl, bannerUrl, category);
                                                songDatabaseRef.child(songId).setValue(song);

                                                ed_song_name.setText("");
                                                ed_artist_name.setText("");
                                                btn_remove_artist.setVisibility(View.INVISIBLE);
                                                iv_select_song_image.setImageResource(R.drawable.placeholder_song);
                                                iv_select_banner_image.setImageResource(R.drawable.placeholder_song);
                                                tv_choose_song.setText("No file selected");
                                                artistList.clear();
                                                imageFilePath = null;
                                                bannerFilePath = null;
                                                songFilePath = null;
                                                spinner_catogories.setSelection(0);
                                                Toast.makeText(getContext(), "Add Success!!!", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage("uploaded " + ((int) progress) + "%.....");
            }
        });


    }

    public String getFileExtension(Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(getActivity().getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }
        return extension;

    }
}
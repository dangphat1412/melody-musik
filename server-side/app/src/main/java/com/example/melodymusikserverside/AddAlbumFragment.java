package com.example.melodymusikserverside;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.melodymusikserverside.Adapter.CatogoriesSpinnerAdapter;
import com.example.melodymusikserverside.Adapter.MyArtistAdapter;
import com.example.melodymusikserverside.Adapter.MySongAdapter;
import com.example.melodymusikserverside.Adapter.SongAdapterAddAlbum;
import com.example.melodymusikserverside.Models.Album;
import com.example.melodymusikserverside.Models.Artist;
import com.example.melodymusikserverside.Models.Catogories;
import com.example.melodymusikserverside.Models.Constants;
import com.example.melodymusikserverside.Models.Song;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class AddAlbumFragment extends Fragment {

    private EditText ed_album_name;
    private LinearLayout ll_select_album_image;
    private ImageView iv_album_image;
    private ImageButton btn_select_song;
    private Button btn_add_new_album;
    private Uri filePath;
    private MySongAdapter adapter;
    private SongAdapterAddAlbum songAdapterAddAlbum;
    private RecyclerView rvSelectedSong;
    private static final int PICK_IMAGE_REQUEST = 234;
    DatabaseReference mDatabase;
    StorageReference storageReference;
    private List<Song> songList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_album, container, false);
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_ALBUM);

        ed_album_name = rootView.findViewById(R.id.ed_album_name);
        ll_select_album_image = rootView.findViewById(R.id.ll_select_album_image);
        iv_album_image = rootView.findViewById(R.id.iv_album_image);
        btn_add_new_album = rootView.findViewById(R.id.btn_add_new_album);
        btn_select_song = rootView.findViewById(R.id.btn_select_song);
        rvSelectedSong = rootView.findViewById(R.id.rvSelectedSong);
        songList = new ArrayList<>();
        ll_select_album_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        btn_select_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog(rootView);
            }
        });

        btn_add_new_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_album_name.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter album name!", Toast.LENGTH_SHORT).show();
                } else if (filePath == null) {
                    Toast.makeText(getActivity(), "Select album image!", Toast.LENGTH_SHORT).show();
                } else if (songList.isEmpty()) {
                    Toast.makeText(getActivity(), "Select songs!", Toast.LENGTH_SHORT).show();
                } else {

                    uploadFile();
                }

            }
        });


        return rootView;
    }


    private void uploadFile() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        HashMap<String, Song> songMap = new HashMap<>();
        for (Song s : songList) {
            songMap.put(s.getId(), s);
        }
        final StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_ALBUM_IMAGES + System.currentTimeMillis() + "." + getFileExtension(filePath));
        sRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        String albumId = mDatabase.push().getKey();
                        Album album = new Album(albumId, ed_album_name.getText().toString().trim(), url, songMap);
                        mDatabase.child(albumId).setValue(album);
                        progressDialog.dismiss();
                        ed_album_name.setText("");
                        iv_album_image.setImageResource(R.drawable.placeholder_song);
                        rvSelectedSong.removeAllViewsInLayout();
                        Toast.makeText(getActivity(), "Update Success", Toast.LENGTH_SHORT).show();
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

    private void showBottomSheetDialog(View rootView) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                getContext(), R.style.BottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(
                        R.layout.bottom_sheet_choose_song,
                        (LinearLayout) rootView.findViewById(R.id.bottomSheetSongContainer)
                );

        RecyclerView rvSongBottomSheet;
        EditText mSearchField;
        ImageButton mSearchBtn;

        mSearchField = bottomSheetView.findViewById(R.id.search_field);
        mSearchBtn = bottomSheetView.findViewById(R.id.search_btn);
        rvSongBottomSheet = bottomSheetView.findViewById(R.id.rvSongBottomSheet);
        rvSongBottomSheet.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<Song> options =
                new FirebaseRecyclerOptions.Builder<Song>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("songs"), Song.class)
                        .build();
        adapter = new MySongAdapter(options);
        adapter.startListening();
        rvSongBottomSheet.setAdapter(adapter);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearchField.getText().toString().trim();
                Toast.makeText(getActivity().getApplicationContext(), searchText, Toast.LENGTH_SHORT).show();
                FirebaseRecyclerOptions<Song> options = new FirebaseRecyclerOptions.Builder<Song>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("songs")
                                .orderByChild("title").startAt(searchText)
                                .endAt(searchText + "\uf8ff"), Song.class)
                        .build();
                adapter = new MySongAdapter(options);
                adapter.startListening();
                rvSongBottomSheet.setAdapter(adapter);

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvSongBottomSheet.setLayoutManager(linearLayoutManager);
        rvSongBottomSheet.addItemDecoration(new DividerItemDecoration(rvSongBottomSheet.getContext(), DividerItemDecoration.VERTICAL));
        ViewGroup.LayoutParams params = rvSongBottomSheet.getLayoutParams();
        params.height = 1000;
        rvSongBottomSheet.setLayoutParams(params);

        rvSongBottomSheet.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rvSongBottomSheet, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Song song = adapter.getItem(position);
                if (songList.contains(song)) {
                    Toast.makeText(getActivity().getApplicationContext(), adapter.getItem(position).getTitle() + " has already selected", Toast.LENGTH_SHORT).show();
                } else {
                    songList.add(song);
                    Toast.makeText(getActivity().getApplicationContext(), "Selected " + adapter.getItem(position).getTitle(), Toast.LENGTH_SHORT).show();
                }


                songAdapterAddAlbum = new SongAdapterAddAlbum(getContext(), songList);
                rvSelectedSong.setAdapter(songAdapterAddAlbum);
                rvSelectedSong.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                rvSelectedSong.addItemDecoration(new DividerItemDecoration(rvSongBottomSheet.getContext(), DividerItemDecoration.VERTICAL));
                ViewGroup.LayoutParams params = rvSelectedSong.getLayoutParams();
                params.height = 1000;
                rvSelectedSong.setLayoutParams(params);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null && data.getData() != null) {
            filePath = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                iv_album_image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
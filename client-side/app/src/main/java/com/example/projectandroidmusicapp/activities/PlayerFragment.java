package com.example.projectandroidmusicapp.activities;


import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.adapter.playlist.PlaylistToAddAdapter;
import com.example.projectandroidmusicapp.entity.Artist;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Playlist;
import com.example.projectandroidmusicapp.entity.Song;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PlayerFragment extends Fragment {


    private boolean isRepeated = false;
    private boolean isShuffle = false;

    private Song song;
    private int currentIndex = -1;
    private Map<Integer, Song> songMapInt;
    private Set<Integer> songsInt;

    private Animation animation;

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private ImageButton ibPlaySongCover, ibPlaySongMain, ibPrevSong, ibNextSong, ibPrevSongCover, ibNextSongCover, ibPlayerAddToPlaylist, ibRepeatSong, ibShuffleSong;
    private ImageView ivPlayerSongMain, ivPlayerSongCover;
    private TextView tvSongTitleCover, tvSongTitleMain, tvSingerNameCover, tvSingerNameMain, tvCurrentTime, tvTotalTime;
    private SeekBar seekBarSongTime;

    private static MediaPlayer mMediaPlayer;

    public static MediaPlayer getmMediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rooterView = inflater.inflate(R.layout.fragment_player, container, false);

        //begin
        songMapInt = new HashMap<>();
        int i = 0;
        if ((Song) getArguments().getSerializable("song") != null && (List<Song>) getArguments().getSerializable("songs") != null) {
            song = (Song) getArguments().getSerializable("song");
            for (Song s : (List<Song>) getArguments().getSerializable("songs")) {
                songMapInt.put(i, s);
                if (song.equals(s)) {
                    currentIndex = i;
                }
                i++;
            }

        }
        songsInt = songMapInt.keySet();


        //Button
        ibPlaySongMain = rooterView.findViewById(R.id.ibPlayMain);
        ibPlaySongCover = rooterView.findViewById(R.id.ibPlayCover);
        ibPrevSong = rooterView.findViewById(R.id.ibPrevSong);
        ibNextSong = rooterView.findViewById(R.id.ibNextSong);
        ibPrevSongCover = rooterView.findViewById(R.id.ibPrevSongCover);
        ibNextSongCover = rooterView.findViewById(R.id.ibNextSongCover);
        ibPlayerAddToPlaylist = rooterView.findViewById(R.id.ibPlayerAddToPlaylist);
        ibRepeatSong = rooterView.findViewById(R.id.ibRepeat);
        ibShuffleSong = rooterView.findViewById(R.id.ibShuffle);

        //Image
        tvSongTitleCover = rooterView.findViewById(R.id.tvSongTitleCover);
        ivPlayerSongMain = rooterView.findViewById(R.id.ivPlayerSongBig);
        ivPlayerSongCover = rooterView.findViewById(R.id.ivPlayerSongCover);

        //SeekBar
        seekBarSongTime = rooterView.findViewById(R.id.seekBarTime);

        //Time
        tvCurrentTime = rooterView.findViewById(R.id.tvCurrentTime);
        tvTotalTime = rooterView.findViewById(R.id.tvTotalTime);

        //Title
        tvSongTitleCover = rooterView.findViewById(R.id.tvSongTitleCover);
        tvSongTitleMain = rooterView.findViewById(R.id.tvSongTitleMain);
        tvSingerNameCover = rooterView.findViewById(R.id.tvSingerNameCover);
        tvSingerNameMain = rooterView.findViewById(R.id.tvSingerNameMain);

        //To Display sliderup
        slidingUpPanelLayout = getActivity().findViewById(R.id.sliderView);
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        //
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //params.addRule(RelativeLayout.ABOVE, R.id.bottom_navigation);
        slidingUpPanelLayout.setLayoutParams(params);

        //Main Image turn around
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotation);
        ivPlayerSongMain.startAnimation(animation);

        //Play music when click song
        playSong();
        songNames();

        //set seek bar
        seekBarSongTime.setMax(mMediaPlayer.getDuration());
        //Set total time
        String totalTime = createTimeLabel(mMediaPlayer.getDuration());
        tvTotalTime.setText(totalTime);
        ibPlaySongMain.setImageResource(R.drawable.ic_pause_black);
        ibPlaySongCover.setImageResource(R.drawable.ic_pause_black);


        //ALL BUTTON
        //Button play in main
        ibPlaySongMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarSongTime.setMax(mMediaPlayer.getDuration());
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    ibPlaySongMain.setImageResource(R.drawable.ic_play_black);
                    ibPlaySongCover.setImageResource(R.drawable.ic_play_black);

                } else {
                    mMediaPlayer.start();
                    ibPlaySongMain.setImageResource(R.drawable.ic_pause_black);
                    ibPlaySongCover.setImageResource(R.drawable.ic_pause_black);

                }
                songNames();
            }
        });

        //Button play in cover
        ibPlaySongCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarSongTime.setMax(mMediaPlayer.getDuration());
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    ibPlaySongMain.setImageResource(R.drawable.ic_play_black);
                    ibPlaySongCover.setImageResource(R.drawable.ic_play_black);

                } else {
                    mMediaPlayer.start();
                    ibPlaySongMain.setImageResource(R.drawable.ic_pause_black);
                    ibPlaySongCover.setImageResource(R.drawable.ic_pause_black);
                }
                songNames();
            }
        });
        ibPlaySongCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarSongTime.setMax(mMediaPlayer.getDuration());
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    ibPlaySongMain.setImageResource(R.drawable.ic_play_black);
                    ibPlaySongCover.setImageResource(R.drawable.ic_play_black);

                } else {
                    mMediaPlayer.start();
                    ibPlaySongMain.setImageResource(R.drawable.ic_pause_black);
                    ibPlaySongCover.setImageResource(R.drawable.ic_pause_black);
                }
                songNames();
            }
        });

        //Button enter to move next song
        ibNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSong();
                playSong();
                songNames();

            }
        });
        ibNextSongCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSong();
                playSong();
                songNames();
            }
        });

        //Button enter to move previous song
        ibPrevSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevSong();
                playSong();
                songNames();
            }
        });
        ibPrevSongCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevSong();
                playSong();
                songNames();
            }
        });

        //add to playList
        //GET PLAYLIST OF USER
        mAuth = FirebaseAuth.getInstance();

        ibPlayerAddToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display in bottom
                if (mAuth.getCurrentUser() == null) {

                    mMediaPlayer.pause();
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                    //Go to Login
                    LoginFragment fragment = new LoginFragment();
                    ((FragmentActivity) getContext()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment).addToBackStack(null)
                            .commit();

                } else {
                    //Bottom sheet add this song to playlist
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                            getContext(), R.style.BottomSheetDialogTheme);
                    View bottomSheetView = LayoutInflater.from(getContext())
                            .inflate(R.layout.playlist_bottom_sheet_add_play_list_layout,
                                    rooterView.findViewById(R.id.bottomSheetContainer));

                    rvListPlaylistToAdd = bottomSheetView.findViewById(R.id.rvListPlaylistToAdd);

                    //Set data to display
                    showPlaylists(bottomSheetDialog);
                    //Display HORIZONTAL
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                    rvListPlaylistToAdd.setLayoutManager(linearLayoutManager);

                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();
                }
            }
        });

        //Repeat Song
        ibRepeatSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRepeated) {
                    ibRepeatSong.setImageResource(R.drawable.ic_repeat_active);
                } else {
                    ibRepeatSong.setImageResource(R.drawable.ic_repeat_inactive);
                }
                isRepeated = !isRepeated;
            }
        });

        //Shuffle SOng
        ibShuffleSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShuffle) {
                    ibShuffleSong.setImageResource(R.drawable.ic_shuffle_active);
                } else {
                    ibShuffleSong.setImageResource(R.drawable.ic_shuffle_inactive);
                }
                isShuffle = !isShuffle;
                songNames();
            }
        });
        return rooterView;
    }

    private void songNames() {
        if (!isShuffle) {
            orderListSong();
        } else {
            randomListSong();
        }
        String totalTime = createTimeLabel(mMediaPlayer.getDuration());
        tvTotalTime.setText(totalTime);

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBarSongTime.setMax(mMediaPlayer.getDuration());
                mMediaPlayer.start();
                ibPlaySongMain.setImageResource(R.drawable.ic_pause);

            }
        });

        //Update data of song in page
        //Artist
        String textArtist = "";
        HashMap<String, Artist> mapArtists = song.getArtists();
        Set<String> artistIds = mapArtists.keySet();
        for (String str : artistIds) {
            textArtist = textArtist + (textArtist.trim().isEmpty() ? "" : ", ") + mapArtists.get(str).getName();
        }

        tvSingerNameCover.setText(textArtist);
        tvSingerNameMain.setText(textArtist);

        //Title Song
        tvSongTitleCover.setText(song.getTitle());
        tvSongTitleMain.setText(song.getTitle());

        //Image Song
        Glide.with(getContext()).load(song.getUrlImage()).into(ivPlayerSongCover);
        Glide.with(getContext()).load(song.getUrlImage()).into(ivPlayerSongMain);

        //When end music
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isRepeated) {
                    mMediaPlayer.start();
                } else {
                    nextSong();
                    playSong();
                    songNames();
                }
            }
        });

        // seekbar duration
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBarSongTime.setMax(mMediaPlayer.getDuration());
                mMediaPlayer.start();
                ibPlaySongMain.setImageResource(R.drawable.ic_pause_black);
                ibPlaySongCover.setImageResource(R.drawable.ic_pause_black);

            }
        });

        seekBarSongTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    seekBarSongTime.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null) {
                    try {
                        if (mMediaPlayer.isPlaying()) {
                            Message message = new Message();
                            message.what = mMediaPlayer.getCurrentPosition();
                            handler.sendMessage(message);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("Handler Leak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int current_position = msg.what;
            seekBarSongTime.setProgress(msg.what);
            String cTime = createTimeLabel(current_position);
            tvCurrentTime.setText(cTime);
        }
    };

    public String createTimeLabel(long duration) {
        String timeLabel = "";
        long min = duration / 1000 / 60;
        long sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;

    }

    public void nextSong() {
        if (mMediaPlayer != null) {
            ibPlaySongMain.setImageResource(R.drawable.ic_pause_black);
        }
        //update current index
        if (currentIndex < songsInt.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        //update song
        song = songMapInt.get(currentIndex);
    }

    public void prevSong() {
        if (mMediaPlayer != null) {
            ibPlaySongMain.setImageResource(R.drawable.ic_pause_black);
        }
        //update current index
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = songsInt.size() - 1;
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        //update song
        song = songMapInt.get(currentIndex);
    }

    public void playSong() {
        try {
            String url = song.getUrlSong(); // your URL here
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare(); // might take long! (for buffering, etc)
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void randomListSong() {
        Integer[] arr = new Integer[songsInt.size()];
        int i = 0;
        for (i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        i = 0;
        Collections.shuffle(Arrays.asList(arr));
        songMapInt = new HashMap<>();
        for (Song s : (List<Song>) getArguments().getSerializable("songs")) {
            songMapInt.put(arr[i], s);
            i++;
        }
        songsInt = songMapInt.keySet();
    }

    public void orderListSong() {
        songMapInt = new HashMap<>();
        int i = 0;
        for (Song s : (List<Song>) getArguments().getSerializable("songs")) {
            songMapInt.put(i, s);
            i++;
        }
        songsInt = songMapInt.keySet();

    }

    RecyclerView rvListPlaylistToAdd;
    PlaylistToAddAdapter playlistToAddAdapter;

    private void showPlaylists(BottomSheetDialog bottomSheetDialog) {

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_PLAYLISTS);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Playlist> playlists = new ArrayList<>();
                    for (DataSnapshot s : snapshot.getChildren()) {
                        Playlist playlist = s.getValue(Playlist.class);
                        if (playlist.getUsers().get(mAuth.getCurrentUser().getUid()) != null) {
                            playlists.add(s.getValue(Playlist.class));
                        }
                    }
                    playlistToAddAdapter = new PlaylistToAddAdapter(getContext(), playlists, song, bottomSheetDialog);
                    rvListPlaylistToAdd.setAdapter(playlistToAddAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
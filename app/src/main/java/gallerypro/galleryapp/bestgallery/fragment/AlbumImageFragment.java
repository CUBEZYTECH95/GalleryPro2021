//package com.example.photogallery.fragment;
//
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.provider.MediaStore;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.example.photogallery.R;
//import com.example.photogallery.adapter.AlbumPictureAdapter;
//import com.example.photogallery.interfaces.AlbumClickListener;
//import com.example.photogallery.model.AlbumPictureModel;
//import com.example.photogallery.viewholder.PictureViewHolder;
//
//import java.util.ArrayList;
//
//
//public class AlbumImageFragment extends Fragment implements AlbumClickListener {
//
//    private String folderPath;
//    private String folderName;
//    private ArrayList<AlbumPictureModel> allPictures;
//    private RecyclerView recyclerView;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_album_image, container, false);
//
//        recyclerView = view.findViewById(R.id.albumImageRecyclerView);
//
//        if (getArguments() != null) {
//            Bundle bundle = new Bundle();
//            folderPath = bundle.getString("folderPath");
//            folderName = bundle.getString("folderName");
//        }
//
//        allPictures = new ArrayList<>();
//
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
//        AlbumPictureAdapter adapter = new AlbumPictureAdapter(getAllImageByFolder(folderPath),getContext(),this);
//
//
//        return view;
//    }
//
//    public ArrayList<AlbumPictureModel> getAllImageByFolder(String path) {
//
//        ArrayList<AlbumPictureModel> images = new ArrayList<>();
//
//        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//        String[] projection = {MediaStore.Images.ImageColumns.DATA,MediaStore.Images.ImageColumns.DISPLAY_NAME,MediaStore.Images.ImageColumns.SIZE};
//
//        Cursor cursor = getContext().getContentResolver().query(uri,projection,MediaStore.Images.ImageColumns.DATA+" like ?",new String[]{"%"+path+"%"},null);
//
//        cursor.moveToFirst();
//        do {
//            AlbumPictureModel pic = new AlbumPictureModel();
//            pic.setPictureName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)));
//
//            pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)));
//
//            pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE)));
//
//            images.add(pic);
//        }while (cursor.moveToNext());
//        cursor.close();
//
//        ArrayList<AlbumPictureModel> reSelection = new ArrayList<>();
//        for (int i = images.size() - 1; i> -1; i--){
//            reSelection.add(images.get(i));
//        }
//
//        images = reSelection;
//
//        return images;
//
//    }
//
//    @Override
//    public void onPicClicked(String pictureFolderPath, String folderName) {
//
//    }
//
//    @Override
//    public void onPicClicked(PictureViewHolder holder, int position, ArrayList<AlbumPictureModel> pics) {
//        Toast.makeText(getContext(), pics.get(position).getPicturePath(), Toast.LENGTH_SHORT).show();
//    }
//}
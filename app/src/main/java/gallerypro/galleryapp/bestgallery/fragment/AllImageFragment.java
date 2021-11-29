package gallerypro.galleryapp.bestgallery.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gallerypro.galleryapp.bestgallery.R;

import gallerypro.galleryapp.bestgallery.interfaces.AllImageClickListener;
import gallerypro.galleryapp.bestgallery.model.AllImagesModel;
import gallerypro.galleryapp.bestgallery.model.DateItem;
import gallerypro.galleryapp.bestgallery.model.ImageItem;
import gallerypro.galleryapp.bestgallery.model.ListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AllImageFragment extends Fragment implements AllImageClickListener {

    private RecyclerView recyclerView;
    private HashMap<String, List<AllImagesModel>> imageHashMap;
    List<ListItem> consolidatedList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_image, container, false);
        recyclerView = view.findViewById(R.id.allImageRecyclerView);


        imageHashMap = groupDataIntoHashMap(getAllImages());



        for (String date : imageHashMap.keySet()) {
            DateItem dateItem = new DateItem();
            Log.d("+++++", "onCreateView: " + date);
            dateItem.setDate(date);
            consolidatedList.add(dateItem);

            for (AllImagesModel allImagesModel : imageHashMap.get(date)) {
                ImageItem imageItem = new ImageItem();
                imageItem.setAllImagesModel(allImagesModel);
                consolidatedList.add(imageItem);
                Log.d("+++++", "onCreateView img: " + allImagesModel.getPath());
            }
        }


//        AllImagesAdapter allImagesAdapter = new AllImagesAdapter(getContext(), getAllImages(),this);
//
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
//        recyclerView.setLayoutManager(gridLayoutManager);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(allImagesAdapter);
//

        return view;
    }




    private List<AllImagesModel> getAllImages() {

        List<AllImagesModel> allImages = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_MODIFIED};

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);


        if (cursor!= null){
            cursor.moveToFirst();
        }

        try {


            do {

                AllImagesModel allImagesModel = new AllImagesModel();

                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED));
                long longDate = Long.parseLong(date);
                Date dates = new Date(longDate * 1000L);
                String dateFormatted = new SimpleDateFormat("dd.MM.yyyy").format(dates);

                allImagesModel.setPath(path);
                allImagesModel.setName(name);
                allImagesModel.setDate(dateFormatted);
                allImages.add(allImagesModel);

            } while (cursor.moveToNext());
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        for (int i = 0; i < allImages.size(); i++) {
            Log.d("TAG", "getAllImages: name " + allImages.get(i).getName() + "\n" + "path " + allImages.get(i).getPath() + "\n" + "date " + allImages.get(i).getDate());
        }

        return allImages;
    }

    private HashMap<String, List<AllImagesModel>> groupDataIntoHashMap(List<AllImagesModel> listOfPojosOfJsonArray) {

        HashMap<String, List<AllImagesModel>> groupedHashMap = new HashMap<>();

        for (AllImagesModel allImagesModel : listOfPojosOfJsonArray) {

            String hashMapKey = allImagesModel.getDate();

            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap.get(hashMapKey).add(allImagesModel);
            } else {
                List<AllImagesModel> list = new ArrayList<>();
                list.add(allImagesModel);
                groupedHashMap.put(hashMapKey, list);
            }
        }


        return groupedHashMap;
    }


    @Override
    public void onPicClicked(List<AllImagesModel> allImagesModelList, int position) {

    }
}
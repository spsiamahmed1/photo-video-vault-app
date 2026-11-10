package com.siam.vault.helper;

import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import com.siam.vault.database.AppDatabase;
import java.io.*;
import java.util.UUID;

public final class FileHelper {
    private FileHelper() {}
    private static File vaultDir(Context c){File f=new File(c.getFilesDir(),"vault");if(!f.exists())f.mkdirs();return f;}
    public static long importUri(Context c,Uri uri,String type,String album)throws Exception{
        ContentResolver cr=c.getContentResolver();String name="Vault item";long size=0;
        try(Cursor cur=cr.query(uri,new String[]{OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE},null,null,null)){if(cur!=null&&cur.moveToFirst()){int n=cur.getColumnIndex(OpenableColumns.DISPLAY_NAME),s=cur.getColumnIndex(OpenableColumns.SIZE);if(n>=0)name=cur.getString(n);if(s>=0&&!cur.isNull(s))size=cur.getLong(s);}}
        String mime=cr.getType(uri);if(mime==null)mime=type.equals("video")?"video/mp4":"image/*";
        File target=new File(vaultDir(c),UUID.randomUUID()+".vlt");try(InputStream in=cr.openInputStream(uri)){if(in==null)throw new IOException("Cannot open selected file");EncryptionHelper.encrypt(in,target);}catch(Exception e){target.delete();throw e;}
        if(size<=0)size=target.length();long id=new AppDatabase(c).addMedia(name,mime,type,album==null||album.trim().isEmpty()?"সাধারণ":album.trim(),target.getAbsolutePath(),size);
        if(id<0){target.delete();throw new IOException("Could not save item metadata");}return id;
    }
    public static File decryptToCache(Context c,AppDatabase.Media m)throws Exception{File out=File.createTempFile("vault_preview_",".tmp",c.getCacheDir());try{EncryptionHelper.decrypt(new File(m.path),out);return out;}catch(Exception e){out.delete();throw e;}}
    public static Bitmap thumbnail(Context c,AppDatabase.Media m){File temp=null;try{temp=decryptToCache(c,m);if("video".equals(m.type)){MediaMetadataRetriever r=new MediaMetadataRetriever();r.setDataSource(temp.getAbsolutePath());Bitmap b=r.getFrameAtTime(1_000_000);r.release();return b==null?null:Bitmap.createScaledBitmap(b,Math.min(512,b.getWidth()),Math.min(512,b.getHeight()),true);}return decodeSampled(temp,512);}catch(Exception e){return null;}finally{if(temp!=null)temp.delete();}}
    public static Bitmap previewBitmap(Context c,AppDatabase.Media m)throws Exception{File temp=null;try{temp=decryptToCache(c,m);return decodeSampled(temp,2048);}finally{if(temp!=null)temp.delete();}}
    private static Bitmap decodeSampled(File file,int maxSide){BitmapFactory.Options bounds=new BitmapFactory.Options();bounds.inJustDecodeBounds=true;BitmapFactory.decodeFile(file.getAbsolutePath(),bounds);int sample=1;while(bounds.outWidth/sample>maxSide||bounds.outHeight/sample>maxSide)sample*=2;BitmapFactory.Options o=new BitmapFactory.Options();o.inSampleSize=sample;return BitmapFactory.decodeFile(file.getAbsolutePath(),o);}
    public static Uri restoreToGallery(Context c,AppDatabase.Media m)throws Exception{
        if(Build.VERSION.SDK_INT<29)throw new IOException("Gallery restore requires Android 10 or later");
        boolean video="video".equals(m.type);String collection=video?MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString():MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
        ContentValues values=new ContentValues();values.put(MediaStore.MediaColumns.DISPLAY_NAME,m.name);values.put(MediaStore.MediaColumns.MIME_TYPE,m.mime);values.put(MediaStore.MediaColumns.RELATIVE_PATH,video?"Movies/Restored Vault":"Pictures/Restored Vault");if(Build.VERSION.SDK_INT>=29)values.put(MediaStore.MediaColumns.IS_PENDING,1);
        Uri uri=c.getContentResolver().insert(Uri.parse(collection),values);if(uri==null)throw new IOException("Could not create gallery item");
        File plain=null;try{plain=decryptToCache(c,m);try(InputStream p=new BufferedInputStream(new FileInputStream(plain));OutputStream out=c.getContentResolver().openOutputStream(uri)){if(out==null)throw new IOException("Cannot write gallery item");byte[] b=new byte[65536];int n;while((n=p.read(b))!=-1)out.write(b,0,n);}}catch(Exception e){c.getContentResolver().delete(uri,null,null);throw e;}finally{if(plain!=null)plain.delete();}
        if(Build.VERSION.SDK_INT>=29){ContentValues done=new ContentValues();done.put(MediaStore.MediaColumns.IS_PENDING,0);c.getContentResolver().update(uri,done,null,null);}return uri;
    }
    public static boolean deleteVaultFile(AppDatabase.Media m){return new File(m.path).delete();}
    public static void eraseCache(Context c){File[] fs=c.getCacheDir().listFiles((d,n)->n.startsWith("vault_preview_"));if(fs!=null)for(File f:fs)f.delete();}
}

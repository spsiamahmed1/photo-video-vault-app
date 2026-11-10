package com.siam.vault.database;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

public class AppDatabase extends SQLiteOpenHelper {
    public static final String DB_NAME="vault.db";
    public static class Media { public long id,size,added,deletedAt; public String name,mime,type,album,path; public boolean deleted; }
    public static class EncryptedNote { public long id,updated; public byte[] titleBytes,bodyBytes; }
    public AppDatabase(Context c){super(c,DB_NAME,null,1);}
    @Override public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE media(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,mime TEXT,type TEXT,album TEXT,path TEXT,size INTEGER,added INTEGER,deleted INTEGER DEFAULT 0,deleted_at INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE notes(id INTEGER PRIMARY KEY AUTOINCREMENT,title BLOB,body BLOB,updated INTEGER)");
        db.execSQL("CREATE TABLE attempts(id INTEGER PRIMARY KEY AUTOINCREMENT,at INTEGER)");
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){}
    public long addMedia(String name,String mime,String type,String album,String path,long size){ ContentValues v=new ContentValues();v.put("name",name);v.put("mime",mime);v.put("type",type);v.put("album",album);v.put("path",path);v.put("size",size);v.put("added",System.currentTimeMillis());return getWritableDatabase().insert("media",null,v); }
    private Media from(Cursor c){Media m=new Media();m.id=c.getLong(c.getColumnIndexOrThrow("id"));m.name=c.getString(c.getColumnIndexOrThrow("name"));m.mime=c.getString(c.getColumnIndexOrThrow("mime"));m.type=c.getString(c.getColumnIndexOrThrow("type"));m.album=c.getString(c.getColumnIndexOrThrow("album"));m.path=c.getString(c.getColumnIndexOrThrow("path"));m.size=c.getLong(c.getColumnIndexOrThrow("size"));m.added=c.getLong(c.getColumnIndexOrThrow("added"));m.deleted=c.getInt(c.getColumnIndexOrThrow("deleted"))!=0;m.deletedAt=c.getLong(c.getColumnIndexOrThrow("deleted_at"));return m;}
    public List<Media> media(String type,boolean deleted,String album){List<Media> out=new ArrayList<>();StringBuilder q=new StringBuilder("SELECT * FROM media WHERE deleted=?");List<String>a=new ArrayList<>();a.add(deleted?"1":"0");if(type!=null){q.append(" AND type=?");a.add(type);}if(album!=null){q.append(" AND album=?");a.add(album);}q.append(" ORDER BY added DESC");try(Cursor c=getReadableDatabase().rawQuery(q.toString(),a.toArray(new String[0]))){while(c.moveToNext())out.add(from(c));}return out;}
    public Media media(long id){try(Cursor c=getReadableDatabase().rawQuery("SELECT * FROM media WHERE id=?",new String[]{String.valueOf(id)})){return c.moveToFirst()?from(c):null;}}
    public List<Media> expiredRecycleItems(long cutoff){List<Media> r=new ArrayList<>();try(Cursor c=getReadableDatabase().rawQuery("SELECT * FROM media WHERE deleted=1 AND deleted_at<?",new String[]{String.valueOf(cutoff)})){while(c.moveToNext())r.add(from(c));}return r;}
    public void softDelete(long id){ContentValues v=new ContentValues();v.put("deleted",1);v.put("deleted_at",System.currentTimeMillis());getWritableDatabase().update("media",v,"id=?",new String[]{String.valueOf(id)});}
    public void restore(long id){ContentValues v=new ContentValues();v.put("deleted",0);v.put("deleted_at",0);getWritableDatabase().update("media",v,"id=?",new String[]{String.valueOf(id)});}
    public void remove(long id){getWritableDatabase().delete("media","id=?",new String[]{String.valueOf(id)});}
    public List<String> albums(){List<String> a=new ArrayList<>();try(Cursor c=getReadableDatabase().rawQuery("SELECT DISTINCT album FROM media WHERE deleted=0 ORDER BY album",null)){while(c.moveToNext())a.add(c.getString(0));}return a;}
    public long totalBytes(){try(Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(size),0) FROM media WHERE deleted=0",null)){return c.moveToFirst()?c.getLong(0):0;}}
    public void logAttempt(){getWritableDatabase().execSQL("INSERT INTO attempts(at) VALUES(?)",new Object[]{System.currentTimeMillis()});}
    public List<Long> attempts(){List<Long> r=new ArrayList<>();try(Cursor c=getReadableDatabase().rawQuery("SELECT at FROM attempts ORDER BY at DESC LIMIT 100",null)){while(c.moveToNext())r.add(c.getLong(0));}return r;}
    public long saveNote(long id,byte[] title,byte[] body){ContentValues v=new ContentValues();v.put("title",title);v.put("body",body);v.put("updated",System.currentTimeMillis());if(id>0){getWritableDatabase().update("notes",v,"id=?",new String[]{String.valueOf(id)});return id;}return getWritableDatabase().insert("notes",null,v);}
    public List<EncryptedNote> notes(){List<EncryptedNote> r=new ArrayList<>();try(Cursor c=getReadableDatabase().rawQuery("SELECT * FROM notes ORDER BY updated DESC",null)){while(c.moveToNext()){EncryptedNote n=new EncryptedNote();n.id=c.getLong(0);n.titleBytes=c.getBlob(1);n.bodyBytes=c.getBlob(2);n.updated=c.getLong(3);r.add(n);}}return r;}
    public void deleteNote(long id){getWritableDatabase().delete("notes","id=?",new String[]{String.valueOf(id)});}
}

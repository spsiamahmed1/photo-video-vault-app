package com.siam.vault.helper;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import com.siam.vault.database.AppDatabase;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.zip.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public final class BackupHelper {
    private static final byte[] MAGIC={'P','V','V','1'}; private static final int SALT=16, IV=12;
    private BackupHelper(){}
    private static SecretKey derive(String pass,byte[] salt)throws Exception{PBEKeySpec s=new PBEKeySpec(pass.toCharArray(),salt,210_000,256);byte[] key=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(s).getEncoded();s.clearPassword();return new SecretKeySpec(key,"AES");}
    private static String enc(String s){return Base64.encodeToString((s==null?"":s).getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP|Base64.URL_SAFE);}
    private static String dec(String s){return new String(Base64.decode(s,Base64.NO_WRAP|Base64.URL_SAFE),StandardCharsets.UTF_8);}
    public static int export(Context c,Uri target,String pass)throws Exception{
        AppDatabase db=new AppDatabase(c);List<AppDatabase.Media> list=db.media(null,false,null);byte[] salt=new byte[SALT],iv=new byte[IV];SecureRandom random=new SecureRandom();random.nextBytes(salt);random.nextBytes(iv);Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.ENCRYPT_MODE,derive(pass,salt),new GCMParameterSpec(128,iv));
        OutputStream raw=c.getContentResolver().openOutputStream(target,"w");if(raw==null)throw new IOException("Cannot write backup");DataOutputStream header=new DataOutputStream(raw);header.write(MAGIC);header.write(salt);header.write(iv);header.flush();ZipOutputStream zip=new ZipOutputStream(new CipherOutputStream(raw,cipher));
        StringBuilder manifest=new StringBuilder();for(AppDatabase.Media m:list)manifest.append("M\t").append(m.id).append("\t").append(enc(m.name)).append("\t").append(enc(m.mime)).append("\t").append(enc(m.type)).append("\t").append(enc(m.album)).append("\t").append(m.size).append("\n");
        zip.putNextEntry(new ZipEntry("manifest.tsv"));zip.write(manifest.toString().getBytes(StandardCharsets.UTF_8));zip.closeEntry();
        for(AppDatabase.Media m:list){File f=new File(m.path);if(!f.exists())continue;zip.putNextEntry(new ZipEntry("media/"+m.id));OutputStream nonClosing=new FilterOutputStream(zip){@Override public void close()throws IOException{flush();}};EncryptionHelper.decryptTo(f,nonClosing);zip.closeEntry();}
        zip.finish();zip.close();return list.size();
    }
    public static int restore(Context c,Uri source,String pass)throws Exception{
        InputStream raw=c.getContentResolver().openInputStream(source);if(raw==null)throw new IOException("Cannot read backup");DataInputStream header=new DataInputStream(raw);byte[] magic=new byte[4];header.readFully(magic);if(!Arrays.equals(magic,MAGIC))throw new IOException("Not a Photo Video Vault backup");byte[] salt=new byte[SALT],iv=new byte[IV];header.readFully(salt);header.readFully(iv);Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.DECRYPT_MODE,derive(pass,salt),new GCMParameterSpec(128,iv));ZipInputStream zip=new ZipInputStream(new CipherInputStream(raw,cipher));
        Map<String,String[]> records=new HashMap<>();ZipEntry entry=zip.getNextEntry();if(entry==null||!"manifest.tsv".equals(entry.getName()))throw new IOException("Backup manifest is missing");String manifest=new String(readAll(zip,2*1024*1024),StandardCharsets.UTF_8);zip.closeEntry();for(String line:manifest.split("\\n")){String[] p=line.split("\\t");if(p.length==7&&p[0].equals("M"))records.put("media/"+p[1],new String[]{dec(p[2]),dec(p[3]),dec(p[4]),dec(p[5]),p[6]});}
        File dir=new File(c.getFilesDir(),"vault");if(!dir.exists())dir.mkdirs();AppDatabase db=new AppDatabase(c);int restored=0;while((entry=zip.getNextEntry())!=null){String name=entry.getName();String[] meta=records.get(name);if(meta!=null&&!entry.isDirectory()){File dest=File.createTempFile("restore_",".vlt",dir);try{EncryptionHelper.encrypt(zip,dest);db.addMedia(meta[0],meta[1],meta[2],meta[3],dest.getAbsolutePath(),Long.parseLong(meta[4]));restored++;}catch(Exception e){dest.delete();throw e;}zip.closeEntry();}else zip.closeEntry();}zip.close();return restored;
    }
    private static byte[] readAll(InputStream in,int max)throws IOException{ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[8192];int n,total=0;while((n=in.read(b))!=-1){total+=n;if(total>max)throw new IOException("Backup manifest is too large");out.write(b,0,n);}return out.toByteArray();}
}

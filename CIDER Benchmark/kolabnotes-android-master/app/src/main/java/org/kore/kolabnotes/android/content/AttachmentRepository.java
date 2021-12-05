package org.kore.kolabnotes.android.content;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import org.kore.kolab.notes.Attachment;
import org.kore.kolabnotes.android.R;
import org.kore.kolabnotes.android.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by koni on 12.03.15.
 */
public class AttachmentRepository {
    // Database fields
    private String[] allColumns = { DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_ACCOUNT,
            DatabaseHelper.COLUMN_ROOT_FOLDER,
            DatabaseHelper.COLUMN_IDNOTE,
            DatabaseHelper.COLUMN_IDATTACHMENT,
            DatabaseHelper.COLUMN_CREATIONDATE,
            DatabaseHelper.COLUMN_FILESIZE,
            DatabaseHelper.COLUMN_FILENAME,
            DatabaseHelper.COLUMN_MIMETYPE};
    private final Context context;

    private final NoteRepository noteRepository;

    public AttachmentRepository(Context context) {
        this.context = context;
        this.noteRepository = new NoteRepository(context);
    }



    public boolean insert(String account, String rootFolder, String noteUID,  Attachment attachment) {

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            SQLiteDatabase database = ConnectionManager.getDatabase(context);
            boolean ret = false;
            try {

                File folder = Utils.getAttachmentDirForNote(context,account,rootFolder,noteUID);

                File file = new File(folder, attachment.getFileName());
                boolean newFile = file.createNewFile();
                if(newFile) {
                    long akttime = System.currentTimeMillis();
                    long rowId = doInsert(database, account, rootFolder, noteUID, attachment, akttime);

                    ret = rowId >= 0;
                    if (ret) {

                        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(attachment.getData()); OutputStream outputStream = new FileOutputStream(file)) {
                            int bytes;
                            byte[] buffer = new byte[1024];
                            while ((bytes = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytes);
                            }

                        } catch (FileNotFoundException e) {
                            Log.e("attachment", "could not find attachement " + file, e);

                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                            final Notification notification = new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_kolabnotes_breeze)
                                    .setContentTitle(context.getResources().getString(R.string.attachment_creation_failed))
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(e.getMessage()))
                                    .setAutoCancel(true).build();

                            notificationManager.notify(Utils.WRITE_REQUEST_CODE, notification);
                        } catch (IOException e) {
                            Log.e("attachment", "problem writing attachment " + file, e);

                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                            final Notification notification = new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_kolabnotes_breeze)
                                    .setContentTitle(context.getResources().getString(R.string.attachment_creation_failed))
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(e.getMessage()))
                                    .setAutoCancel(true).build();

                            notificationManager.notify(Utils.WRITE_REQUEST_CODE, notification);
                        }

                        noteRepository.updateAuditInformation(account, rootFolder, noteUID, akttime);
                    }else{
                        Toast.makeText(context, context.getResources().getString(R.string.attachment_already_exist), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, context.getResources().getString(R.string.attachment_already_exist), Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Log.e("attachment", "problem creating file", e);
            }
            return ret;
        }
        return false;
    }

    private long doInsert(SQLiteDatabase db, String account, String rootFolder, String noteUID, Attachment attachment, long aktTime){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ACCOUNT,account);
        values.put(DatabaseHelper.COLUMN_ROOT_FOLDER,rootFolder);
        values.put(DatabaseHelper.COLUMN_IDNOTE, noteUID);
        values.put(DatabaseHelper.COLUMN_IDATTACHMENT, attachment.getId());
        values.put(DatabaseHelper.COLUMN_FILESIZE,attachment.getData().length);
        values.put(DatabaseHelper.COLUMN_FILENAME, attachment.getFileName());
        values.put(DatabaseHelper.COLUMN_MIMETYPE, attachment.getMimeType());
        values.put(DatabaseHelper.COLUMN_CREATIONDATE, aktTime);

        return db.insert(DatabaseHelper.TABLE_ATTACHMENT, null, values);
    }

    public void delete(String account, String rootFolder, String noteUID, Attachment attachment) {
        ConnectionManager.getDatabase(context).delete(DatabaseHelper.TABLE_ATTACHMENT,
                DatabaseHelper.COLUMN_ACCOUNT + " = '" + account + "' AND " +
                        DatabaseHelper.COLUMN_ROOT_FOLDER + " = '" + rootFolder + "' AND " +
                        DatabaseHelper.COLUMN_IDNOTE + " = '" + noteUID + "' AND " +
                        DatabaseHelper.COLUMN_IDATTACHMENT + " = '" + attachment.getId() + "' ",
                null);


        File filesDir = Utils.getAttachmentDirForNote(context,account,rootFolder,noteUID);
        File file = new File(filesDir,attachment.getFileName());
        if(file.exists()){
            file.delete();
        }

        ModificationRepository modificationRepository = new ModificationRepository(context);
        Modification modification = modificationRepository.getUnique(account,rootFolder,attachment.getId());

        if(modification == null){
            modificationRepository.insert(account, rootFolder, attachment.getId(), ModificationRepository.ModificationType.DEL, noteUID, Modification.Descriminator.ATTACHMENT);
        }
    }

    public void deleteForNote(String account, String rootFolder, String noteUID) {
        ConnectionManager.getDatabase(context).delete(DatabaseHelper.TABLE_ATTACHMENT,
                DatabaseHelper.COLUMN_ACCOUNT + " = '" + account + "' AND " +
                        DatabaseHelper.COLUMN_ROOT_FOLDER + " = '" + rootFolder + "' AND " +
                        DatabaseHelper.COLUMN_IDNOTE + " = '" + noteUID + "' ",
                null);


        File folder = Utils.getAttachmentDirForNote(context, account, rootFolder, noteUID);
        deleteAttachmentsFromFolder(folder);

        noteRepository.updateAuditInformation(account, rootFolder, noteUID, System.currentTimeMillis());
    }

    public void cleanAccount(String account, String rootFolder) {
        ConnectionManager.getDatabase(context).delete(DatabaseHelper.TABLE_ATTACHMENT,
                DatabaseHelper.COLUMN_ACCOUNT + " = '" + account + "' AND " +
                        DatabaseHelper.COLUMN_ROOT_FOLDER + " = '" + rootFolder + "' ",
                null);
        
        File folder = Utils.getAttachmentDirForAccount(context, account, rootFolder);
        deleteAttachmentsFromFolder(folder);
    }

    public Uri getUriFromAttachment(String account, String rootFolder, String noteUID, Attachment attachment){
        File filesDir = Utils.getAttachmentDirForNote(context, account, rootFolder, noteUID);
        File file = new File(filesDir,attachment.getFileName());

        return FileProvider.getUriForFile(
                context,
                "kore.kolabnotes.fileprovider",
                file);

    }


    public Attachment getAttachmentWithAttachmentID(String account, String rootFolder, String noteUID, String attachmentid){
        Cursor cursor = ConnectionManager.getDatabase(context).query(DatabaseHelper.TABLE_ATTACHMENT,
                allColumns,
                DatabaseHelper.COLUMN_ACCOUNT + " = '" + account + "' AND " +
                        DatabaseHelper.COLUMN_ROOT_FOLDER + " = '" + rootFolder + "' AND " +
                        DatabaseHelper.COLUMN_IDNOTE + " = '" + noteUID + "' AND " +
                        DatabaseHelper.COLUMN_IDATTACHMENT + " = '" + attachmentid + "' ",
                null,
                null,
                null,
                null);

        Attachment attachment = null;
        if(cursor.moveToNext()){
            attachment = cursorToAttachmment(cursor, true);
        }

        return attachment;
    }

    public List<Attachment> getAllForNote(String account, String rootFolder, String noteUid, boolean withFile) {
        List<Attachment> attachments = new ArrayList<Attachment>();

        Cursor cursor = ConnectionManager.getDatabase(context).query(DatabaseHelper.TABLE_ATTACHMENT,
                allColumns,
                DatabaseHelper.COLUMN_ACCOUNT + " = '" + account + "' AND " +
                        DatabaseHelper.COLUMN_ROOT_FOLDER + " = '" + rootFolder + "' AND " +
                        DatabaseHelper.COLUMN_IDNOTE + " = '" + noteUid + "' ",
                null,
                null,
                null,
                null);

        while (cursor.moveToNext()) {
            attachments.add(cursorToAttachmment(cursor, withFile));
        }
        cursor.close();
        return attachments;
    }

    public boolean hasNoteAttachments(String account, String rootFolder, String noteUid) {
        Cursor cursor = ConnectionManager.getDatabase(context).query(DatabaseHelper.TABLE_ATTACHMENT,
                allColumns,
                DatabaseHelper.COLUMN_ACCOUNT + " = '" + account + "' AND " +
                        DatabaseHelper.COLUMN_ROOT_FOLDER + " = '" + rootFolder + "' AND " +
                        DatabaseHelper.COLUMN_IDNOTE + " = '" + noteUid + "' ",
                null,
                null,
                null,
                null);

        boolean ret = cursor.moveToNext();
        cursor.close();
        return ret;
    }

    public Set<String> getNoteIDsWithAttachments(String account, String rootFolder) {
        Cursor cursor = ConnectionManager.getDatabase(context).query(DatabaseHelper.TABLE_ATTACHMENT,
                new String[]{DatabaseHelper.COLUMN_IDNOTE},
                DatabaseHelper.COLUMN_ACCOUNT + " = '" + account + "' AND " +
                        DatabaseHelper.COLUMN_ROOT_FOLDER + " = '" + rootFolder + "' ",
                null,
                null,
                null,
                null);

        HashSet<String> ids = new HashSet<>();
        while(cursor.moveToNext()){
            ids.add(cursor.getString(0));
        }

        cursor.close();
        return ids;
    }

    public boolean attachmentsCreatedAfterLastSync(String account, String rootFolder, String noteUID, Date date) {
        List<Attachment> attachments = new ArrayList<Attachment>();

        Cursor cursor = ConnectionManager.getDatabase(context).query(DatabaseHelper.TABLE_ATTACHMENT,
                allColumns,
                DatabaseHelper.COLUMN_ACCOUNT + " = '" + account + "' AND " +
                        DatabaseHelper.COLUMN_ROOT_FOLDER + " = '" + rootFolder + "' AND " +
                        DatabaseHelper.COLUMN_IDNOTE + " = '" + noteUID + "' AND " +
                        DatabaseHelper.COLUMN_CREATIONDATE + " > " + date.getTime(),
                null,
                null,
                null,
                null);

        if (cursor.moveToNext()) {
            return true;
        }
        cursor.close();
        return false;
    }

    private void deleteAttachmentsFromFolder(File directory) {
        if(directory.exists()){
            String[] children = directory.list();
            for (int i = 0; i < children.length; i++)
            {
                File file = new File(directory, children[i]);
                if(file.isDirectory()){
                    deleteAttachmentsFromFolder(file);
                }else{
                    file.delete();
                }
            }

            directory.delete();
        }
    }

    private Attachment cursorToAttachmment(Cursor cursor, boolean withFile){
        String account = cursor.getString(1);
        String rootFolder = cursor.getString(2);
        String noteUID = cursor.getString(3);
        String id = cursor.getString(4);
        int filesize = cursor.getInt(6);
        String filename = cursor.getString(7);
        String mimetype = cursor.getString(8);

        Attachment attachment = new Attachment(id,filename,mimetype);

        if(withFile && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            File filesDir = Utils.getAttachmentDirForNote(context,account,rootFolder,noteUID);
            File file = new File(filesDir,filename);
            try(FileInputStream inputStream = new FileInputStream(file); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
                int bytes;
                byte[] buffer = new byte[1024];
                while ((bytes = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytes);
                }

                attachment.setData(outputStream.toByteArray());
            }catch (FileNotFoundException e){
                Log.e("attachment","could not find attachement "+file, e);
                attachment.setData(new byte[0]);
            } catch (IOException e) {
                Log.e("attachment", "problem loading attachment " + file, e);
                attachment.setData(new byte[0]);
            }
        }else{
            attachment.setData(new byte[filesize]);
        }

        return attachment;
    }
}

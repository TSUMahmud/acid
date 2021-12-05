package org.kore.kolabnotes.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kore.kolab.notes.Note;
import org.kore.kolab.notes.Tag;
import org.kore.kolabnotes.android.NoteSortingComparator;
import org.kore.kolabnotes.android.R;
import org.kore.kolabnotes.android.Utils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class NoteAdapter extends SelectableAdapter<NoteAdapter.ViewHolder> {

    private List<Note> notes;
    private int rowLayout;
    private Context context;
    private ViewHolder.ClickListener clickListener;
    private DateFormat dateFormatter;
    private int COLOR_SELECTED_NOTE;
    private Set<String> notesWithAttachment;

    private List<ViewHolder> views;

    public NoteAdapter(List<Note> notes, int rowLayout, Context context, ViewHolder.ClickListener clickListener, Set<String> notesWithAttachment) {
        this.notes = notes;
        this.rowLayout = rowLayout;
        this.context = context;
        this.clickListener = clickListener;
        this.dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        views = new ArrayList<>(notes.size());
        COLOR_SELECTED_NOTE = ContextCompat.getColor(context, R.color.theme_selected_notes);
        this.notesWithAttachment = notesWithAttachment;
    }

    public void clearNotes() {
        int size = this.notes.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                notes.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void deleteNotes(List<Note> notes) {
        this.notes.removeAll(notes);
        this.notifyDataSetChanged();
    }

    public void addNotes(List<Note> notes) {
        this.notes.addAll(notes);
        Collections.sort(this.notes, new NoteSortingComparator(Utils.getNoteSorting(context)));
        this.notifyItemRangeInserted(0, notes.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);

        return new ViewHolder(v, clickListener, notes);
    }


    void setNotePreview(Note note, ViewHolder viewHolder){
        final String description = note.getDescription();

        if(description == null || description.trim().length() == 0){
            viewHolder.notePreview.setText(context.getResources().getString(R.string.empty_note_description));
            return;
        }

        Spanned text = Html.fromHtml(Utils.getHtmlBodyText(description));

        CharSequence displayText = text;
        if(displayText.length() > 200){
            displayText = displayText.subSequence(0,200) + " ...";
        }

        viewHolder.notePreview.setText(displayText, TextView.BufferType.SPANNABLE);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        boolean isSelected = SelectableAdapter.getSelectedItems().contains(i) ? true : false;
        final Note note = notes.get(i);
        viewHolder.name.setText(note.getSummary());
        viewHolder.classification.setText(context.getResources().getString(R.string.classification)+": "+note.getClassification());
        viewHolder.createdDate.setText(context.getResources().getString(R.string.creationDate)+": "+ dateFormatter.format(note.getAuditInformation().getCreationDate()));
        viewHolder.modificationDate.setText(context.getResources().getString(R.string.modificationDate) + ": " + dateFormatter.format(note.getAuditInformation().getLastModificationDate()));
        viewHolder.categories.removeAllViews();
        setNotePreview(note,viewHolder);

        boolean useLightColor = Utils.useLightTextColor(context, note.getColor());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(12, 0, 0, 0);



        if(note.getCategories().isEmpty()){
            TextView textView = new TextView(context);
            textView.setText(context.getResources().getString(R.string.notags));
            if (isSelected) {
                textView.setTextColor(Color.BLACK);
            } else {
                textView.setTextColor(useLightColor ? Color.WHITE : Color.BLACK);
            }
            viewHolder.categories.addView(textView);
        }else{
            TextView textView = new TextView(context);
            textView.setText(context.getResources().getString(R.string.tags));
            if (isSelected) {
                textView.setTextColor(Color.BLACK);
            } else {
                textView.setTextColor(useLightColor ? Color.WHITE : Color.BLACK);
            }
            viewHolder.categories.addView(textView);

            ArrayList<Tag> sorted = new ArrayList<>(note.getCategories());

            Collections.sort(sorted);

            for(Tag tag : sorted){
                if(tag.getColor() == null){
                    TextView tagTextView = new TextView(context);
                    tagTextView.setText(tag.getName());
                    int backgroundColor;
                    if (isSelected) {
                        backgroundColor = COLOR_SELECTED_NOTE;
                    } else {
                        backgroundColor = note.getColor() == null ? Color.WHITE : Color.parseColor(note.getColor().getHexcode());
                    }
                    tagTextView.setTextColor(useLightColor ? Color.WHITE : Color.BLACK);
                    final Drawable drawable = context.getResources().getDrawable(R.drawable.color_background_with_dashedborder).mutate();

                    drawable.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
                    tagTextView.setBackground(drawable);
                    tagTextView.setLayoutParams(params);

                    viewHolder.categories.addView(tagTextView);
                }else{
                    boolean useLight = Utils.useLightTextColor(context, tag.getColor());

                    TextView tagTextView = new TextView(context);
                    tagTextView.setText(tag.getName());
                    tagTextView.setTextColor(useLight ? Color.WHITE : Color.BLACK);
                    final Drawable drawable = context.getResources().getDrawable(R.drawable.color_background_with_border).mutate();
                    drawable.setColorFilter(Color.parseColor(tag.getColor().getHexcode()), PorterDuff.Mode.MULTIPLY);
                    tagTextView.setBackground(drawable);
                    tagTextView.setLayoutParams(params);

                    viewHolder.categories.addView(tagTextView);
                }
            }
        }

        /* If note selected */
        if (isSelected) {
            viewHolder.cardView.setCardBackgroundColor(COLOR_SELECTED_NOTE);
            viewHolder.name.setBackgroundColor(COLOR_SELECTED_NOTE);
            viewHolder.classification.setBackgroundColor(COLOR_SELECTED_NOTE);
            viewHolder.createdDate.setBackgroundColor(COLOR_SELECTED_NOTE);
            viewHolder.modificationDate.setBackgroundColor(COLOR_SELECTED_NOTE);
            viewHolder.categories.setBackgroundColor(COLOR_SELECTED_NOTE);
            viewHolder.notePreview.setBackgroundColor(COLOR_SELECTED_NOTE);

            viewHolder.name.setTextColor(Color.BLACK);
            viewHolder.classification.setTextColor(Color.BLACK);
            viewHolder.createdDate.setTextColor(Color.BLACK);
            viewHolder.modificationDate.setTextColor(Color.BLACK);
            viewHolder.notePreview.setTextColor(Color.BLACK);
        } else {
            if(note != null && note.getColor() != null){
                viewHolder.cardView.setCardBackgroundColor(Color.parseColor(note.getColor().getHexcode()));
                viewHolder.name.setBackgroundColor(Color.parseColor(note.getColor().getHexcode()));
                viewHolder.classification.setBackgroundColor(Color.parseColor(note.getColor().getHexcode()));
                viewHolder.createdDate.setBackgroundColor(Color.parseColor(note.getColor().getHexcode()));
                viewHolder.modificationDate.setBackgroundColor(Color.parseColor(note.getColor().getHexcode()));
                viewHolder.categories.setBackgroundColor(Color.parseColor(note.getColor().getHexcode()));
                viewHolder.notePreview.setBackgroundColor(Color.parseColor(note.getColor().getHexcode()));

            /*
            * Text color depending on background color:
            * If spectrum from cyan to red and saturation greater than or equal to 0.5 - text is white.
            * If spectrum is not included in these borders or brightness greater than or equal to 0.8 - text is black.
            */
                if (useLightColor) {
                    viewHolder.name.setTextColor(Color.WHITE);
                    viewHolder.classification.setTextColor(Color.WHITE);
                    viewHolder.createdDate.setTextColor(Color.WHITE);
                    viewHolder.modificationDate.setTextColor(Color.WHITE);
                    viewHolder.notePreview.setTextColor(Color.WHITE);
                } else {
                    viewHolder.name.setTextColor(Color.BLACK);
                    viewHolder.classification.setTextColor(Color.GRAY);
                    viewHolder.createdDate.setTextColor(Color.GRAY);
                    viewHolder.modificationDate.setTextColor(Color.GRAY);
                    viewHolder.notePreview.setTextColor(Color.GRAY);
                }

            } else {
                viewHolder.cardView.setCardBackgroundColor(Color.WHITE);
                viewHolder.name.setBackgroundColor(Color.WHITE);
                viewHolder.classification.setBackgroundColor(Color.WHITE);
                viewHolder.createdDate.setBackgroundColor(Color.WHITE);
                viewHolder.modificationDate.setBackgroundColor(Color.WHITE);
                viewHolder.categories.setBackgroundColor(Color.WHITE);
                viewHolder.notePreview.setBackgroundColor(Color.WHITE);

                viewHolder.name.setTextColor(Color.BLACK);
                viewHolder.classification.setTextColor(Color.GRAY);
                viewHolder.createdDate.setTextColor(Color.GRAY);
                viewHolder.modificationDate.setTextColor(Color.GRAY);
                viewHolder.notePreview.setTextColor(Color.GRAY);
            }
        }
        Utils.setElevation(viewHolder.cardView, 5);

        if(Utils.getShowMetainformation(context)){
            viewHolder.showMetainformation();
        }else{
            viewHolder.hideMetainformation();
        }

        if(Utils.getShowPreview(context)){
            viewHolder.showPreview();
        }else{
            viewHolder.hidePreview();
        }

        if(Utils.getShowCharacteristics(context)){
            viewHolder.showCharacteristics();
        }else{
            viewHolder.hideCharacteristics();
        }

        if(notesWithAttachment.contains(note.getIdentification().getUid())){
            viewHolder.attachmentImage.setVisibility(View.VISIBLE);

            if(useLightColor){
                viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attach_file_white_24dp));
            }
        }
    }

    public void setNotesWithAttachment(Set<String> ids){
        this.notesWithAttachment = ids;
    }

    public void restoreElevation(RecyclerView recyclerView){
        for(int i=0; i < recyclerView.getChildCount(); i++){
            final View childAt = recyclerView.getChildAt(i);
            Utils.setElevation(childAt,5);
            if(i == recyclerView.getChildAdapterPosition(childAt)){
                Utils.setElevation(childAt,30);
            }
        }
    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    public boolean isEmpty() {
        return notes.isEmpty() ? true : false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView name;
        TextView classification;
        TextView createdDate;
        TextView modificationDate;
        TextView notePreview;
        LinearLayout categories;
        CardView cardView;
        ImageView attachmentImage;
        private ClickListener listener;
        private List<Note> notes;

        public ViewHolder(View itemView, ClickListener listener, List<Note> notes) {
            super(itemView);

            this.notes = notes;
            this.listener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            name = (TextView) itemView.findViewById(R.id.noteSummary);
            classification = (TextView) itemView.findViewById(R.id.classification);
            createdDate = (TextView) itemView.findViewById(R.id.createdDate);
            modificationDate = (TextView) itemView.findViewById(R.id.modificationDate);
            categories = (LinearLayout) itemView.findViewById(R.id.categories);
            cardView = (CardView)itemView;
            attachmentImage = (ImageView)itemView.findViewById(R.id.attachmentHint);
            notePreview = (TextView)itemView.findViewById(R.id.notePreview);
        }

        @Override
        public void onClick(View v) {
            if (listener != null && getAdapterPosition() >= 0) {
                ViewParent parent = v.getParent();
                if(parent instanceof RecyclerView){
                    RecyclerView recyclerView = (RecyclerView)parent;
                    for(int i=0; i < recyclerView.getChildCount(); i++){
                        final View childAt = recyclerView.getChildAt(i);
                        Utils.setElevation(childAt,5);

                        if(recyclerView.getChildAdapterPosition(childAt) == getAdapterPosition()){
                            Utils.setElevation(childAt,30);
                        }
                    }
                }
                listener.onItemClicked(getAdapterPosition(), notes.get(getAdapterPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                ViewParent parent = v.getParent();
                if(parent instanceof RecyclerView){
                    RecyclerView recyclerView = (RecyclerView)parent;
                    for(int i=0; i < recyclerView.getChildCount(); i++){
                        Utils.setElevation(recyclerView.getChildAt(i),5);
                    }
                }

                return listener.onItemLongClicked(getAdapterPosition(), notes.get(getAdapterPosition()));
            }
            return false;
        }

        public interface ClickListener {
            void onItemClicked(int position, Note note);
            boolean onItemLongClicked(int position, Note note);
        }

        void hideMetainformation(){
            createdDate.setVisibility(View.GONE);
            modificationDate.setVisibility(View.GONE);
        }

        void showMetainformation(){
            createdDate.setVisibility(View.VISIBLE);
            modificationDate.setVisibility(View.VISIBLE);
        }

        void hideCharacteristics(){
            classification.setVisibility(View.GONE);
            categories.setVisibility(View.GONE);
        }

        void showCharacteristics(){
            //issue #85
            classification.setVisibility(View.GONE);
            categories.setVisibility(View.VISIBLE);
        }

        void hidePreview(){
            notePreview.setVisibility(View.GONE);
        }

        void showPreview(){
            notePreview.setVisibility(View.VISIBLE);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.feel.truerenameaudio;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;

/**
 *
 * @author Anton
 */
public class InAudio extends Thread{
    
    private List<File> tracks;
    private String filePath;
    int progress = 0;
    private JProgressBar jProgress;

   
    
    
    public InAudio(List<File> listAudioFiles, String filePath){
        this.tracks = listAudioFiles;
        this.filePath = filePath;   
    }
    
    public InAudio(List<File> listAudioFiles){
        this.tracks = listAudioFiles;        
    }
    
    public InAudio(){       
    }
    
    @Override
    public void run() {        
      
        this.Rename();
     
    }
    
    public void Rename() {
        if(tracks.isEmpty()) return;
        AudioFile enyF = new AudioFile();
        String artist = null;
        String albumArtist = null;
        String trackName = null;
        String encod = null;
        String thisFilePath = null;
        Tag tag;
        
        
        
        
        for(int i = 0; i < tracks.size(); i++){
            setProgress(i+1);
            try {
                enyF = AudioFileIO.read(tracks.get(i));
            }
            catch (CannotReadException | IOException | TagException | 
                    ReadOnlyFileException | InvalidAudioFrameException ex) {
                Logger.getLogger(InAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            tag = enyF.getTag();
            
            if(tag == null) continue;
            
            thisFilePath = tracks.get(i).getParent();
            encod = "." + enyF.getAudioHeader().getEncodingType();
            artist = tag.getFirst(FieldKey.ARTIST);
            albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST);
            trackName = tag.getFirst(FieldKey.TITLE);
            
            if((!artist.isEmpty() || !albumArtist.isEmpty()) && !trackName.isEmpty()){
                
                File newName = !artist.isEmpty() ? 
                    new File(thisFilePath + "\\" + artist + " - " + trackName + encod) :
                    new File(thisFilePath + "\\" + albumArtist + " - " + trackName + encod);
                tracks.get(i).renameTo(newName);               
            }else{
                
                if(!trackName.isEmpty()){
                    
                    File newName = new File(thisFilePath + "\\" + trackName + encod);
                    tracks.get(i).renameTo(newName);
                }else{
                    File newName = !artist.isEmpty() ? 
                        new File(thisFilePath + "\\" + artist + encod) :
                        new File(thisFilePath + "\\" + albumArtist + encod);
                }
            }                       
        }       
    }   
    
    public String Recharseting(String oldStr, String format){
        
        Charset cset = Charset.forName(format);
        ByteBuffer buf = cset.encode(oldStr);
        byte[] b = buf.array();
        String str = new String(b);
        return str;       
    }

    public List<File> getTracks() {
        return tracks;
    }

    public void setTracks(List<File> tracks) {
        this.tracks = tracks;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public int getProgress(){       
        return progress;
    }
    
    public void setProgress( int p){
        int maxSize = tracks.size();
        int currentPer = (Integer)(p * 100) / maxSize;
        jProgress.setValue(currentPer);
    }
    
     public JProgressBar getJProgress() {
        return jProgress;
    }

    public void setJProgress(JProgressBar jProgress) {
        this.jProgress = jProgress;
    }
    
    
}

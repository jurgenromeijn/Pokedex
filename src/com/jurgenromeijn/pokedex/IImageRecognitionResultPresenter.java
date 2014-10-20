package com.jurgenromeijn.pokedex;

/**
 * Any object that implements this interface should be able to do something with the entity
 * recogniced in an image.
 */
public interface IImageRecognitionResultPresenter {
    public void showImageRecognitionResult(String result);
}

package com.example.getdown;

import static java.lang.System.out;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {

    public static <T> void serialize(final T objectToSerialize, final String fileName)
    {
        if (fileName == null)
        {
            throw new IllegalArgumentException(
                    "Name of file to which to serialize object to cannot be null.");
        }
        if (objectToSerialize == null)
        {
            throw new IllegalArgumentException("Object to be serialized cannot be null.");
        }
        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos))
        {
            oos.writeObject(objectToSerialize);
            out.println("Serialization of Object " + objectToSerialize + " completed.");
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    /**
     * Provides an object deserialized from the file indicated by the provided
     * file name.
     *
     * @param <T> Type of object to be deserialized.
     * @param fileToDeserialize Name of file from which object is to be deserialized.
     * @param classBeingDeserialized Class definition of object to be deserialized
     *    from the file of the provided name/path; it is recommended that this
     *    class define its own toString() implementation as that will be used in
     *    this method's status output.
     * @return Object deserialized from provided filename as an instance of the
     *    provided class; may be null if something goes wrong with deserialization.
     * @throws IllegalArgumentException Thrown if either provided parameter is null.
     */
    public static <T> T deserialize(final String fileToDeserialize, final Class<T> classBeingDeserialized)
    {
        if (fileToDeserialize == null)
        {
            throw new IllegalArgumentException("Cannot deserialize from a null filename.");
        }
        if (classBeingDeserialized == null)
        {
            throw new IllegalArgumentException("Type of class to be deserialized cannot be null.");
        }
        T objectOut = null;
        try (FileInputStream fis = new FileInputStream(fileToDeserialize);
             ObjectInputStream ois = new ObjectInputStream(fis))
        {
            objectOut = (T) ois.readObject();
            out.println("Deserialization of Object " + objectOut + " is completed.");
        }
        catch (IOException | ClassNotFoundException exception)
        {
            exception.printStackTrace();
        }
        return objectOut;
    }

}

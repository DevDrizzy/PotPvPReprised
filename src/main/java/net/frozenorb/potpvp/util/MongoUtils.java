package net.frozenorb.potpvp.util;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import net.frozenorb.potpvp.PotPvPSI;

import org.bson.Document;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class MongoUtils {

    /**
     * UpdateOptions used to perform an upsert (see https://docs.mongodb.com/manual/reference/method/db.collection.update/)
     */
    public static final UpdateOptions UPSERT_OPTIONS = new UpdateOptions().upsert(true);

    /**
     * Convenience method to retrieve a MongoCollection object from singleton
     * MongoDatabase.
     *
     * @param collectionId Id of collection to retrieve
     * @return MongoCollection for specified collection id.
     */
    public static MongoCollection<Document> getCollection(String collectionId) {
        return PotPvPSI.getInstance().getMongoDatabase().getCollection(collectionId);
    }

}
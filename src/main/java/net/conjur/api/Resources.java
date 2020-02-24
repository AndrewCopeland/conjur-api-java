package net.conjur.api;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class Resources {

    private ArrayList<Resource> resources;

    public Resources() {
        resources = new ArrayList<Resource>();
    }

    public Resources(ArrayList<Resource> resources) {
        this.resources = resources;
    }

    public static Resources fromJsonArray(JsonArray jsonResources) {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        for ( JsonElement resource : jsonResources) {
            JsonObject obj = resource.getAsJsonObject();
            resources.add(Resource.fromJsonObject(obj));
        }
        return new Resources(resources);
    }

    /**
     * Returns a Resources instance with the kind filtered
     * This will not reach out to conjur but will filter the 
     * already existant Resources object.
     * @return The resources object that meet the filter of kind and search
     */
    public Resources filter(ResourceKind kind) {
        return filter(kind, null);
    }

    /**
     * Returns a Resources instance with the kind and search filters
     * This will not reach out to conjur but will filter the 
     * already existant Resources object.
     * @return The resources object that meet the filter of kind and search
     */
    public Resources filter(ResourceKind kind, String search) {
        ArrayList<Resource> filteredResources = new ArrayList<Resource>();

        for(Resource resource : resources) {
            Resource filteredResource = resource;

            // if filtering on kind and kind matches
            if(kind != null) {
                if (resource.getKind() == kind) {
                    filteredResource = resource;
                } else {
                    filteredResource = null;
                }
            }

            // if filtering kind was correct and filtering on search
            if(filteredResource != null && search != null) {
                if (resource.getFullId().contains(search)) {
                    filteredResource = resource;
                } else {
                    filteredResource = null;
                }
            }

            // as long as filtered resource is not null add
            if (filteredResource != null) {
                filteredResources.add(filteredResource);
            }
        }

        return new Resources(filteredResources);
    }

    /**
     * Return one resource from the full ID of the resource
     * If a resource cannot be found then null is returned
     * @return The resource object or null if not found
     */
    public Resource get(String fullId) {
        for(Resource resource : resources) {
            if(resource.getFullId().equals(fullId)) {
                return resource;
            }
        }
        return null;
    }

    /**
     * Return one resource from the ResourceKind and resource ID
     * If a resource cannot be found then null is returned
     * @return The resource object or null if not found
     */
    public Resource get(ResourceKind kind, String id) {
        for(Resource resource : resources) {
            if(resource.getKind().equals(kind) && resource.getId().equals(id)) {
                return resource;
            }
        }
        return null;
    }

    /**
     * Return one resource from the resources list using index
     * @return The resource object or throws IndexOutOfBoundsException
     */
    public Resource getIndex(int index) {
        return resources.get(index);
    }

    /**
     * Return all resources as an array list
     * @return The resources in an array list
     */
    public ArrayList<Resource> asArrayList() {
        return resources;
    }

    /**
     * Return length of resources.
     */
    public int getLength() {
        return resources.toArray().length;
    }
}

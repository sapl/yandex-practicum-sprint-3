#!/bin/bash

# List of images to transfer
images=("sh-users" "sh-device-core" "sh-monolith" "sh-telemetry")

# Export each image to a .tar file and load it into Minikube
for image in "${images[@]}"; do
    # Export the image to a .tar file
    echo "Exporting image $image to ${image}.tar..."
    docker save -o "${image}.tar" "${image}:latest"

    # Copy the .tar file to Minikube
    echo "Copying ${image}.tar to Minikube..."
    minikube cp "${image}.tar" "/home/docker/${image}.tar"

    # Load the image into Minikube
    echo "Loading image $image in Minikube..."
    minikube ssh "docker load -i /home/docker/${image}.tar"

    # Delete the local .tar file after copying (optional)
    rm "${image}.tar"
done

echo "All images have been successfully transferred and loaded into Minikube."

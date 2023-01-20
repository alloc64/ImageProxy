# Image Proxy

Image resizing proxy with S3 compatible storage. 
Supports jpg and png files. 

## Usage
Specify S3 storage provider:

> image.proxy.bucket.region=europe-central2
> image.proxy.s3.accesskey=your access key
> image.proxy.s3.endpoint=https://storage.googleapis.com
> image.proxy.s3.secretkey=secret

## Run

> docker-compose up

Get resized image:
> http://localhost:8080/img/from-url/{{specified image url}}?w=100
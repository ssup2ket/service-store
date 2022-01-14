#!/bin/bash
# -- Variables --
EXIT_CODE=0
ADMIN_ACCESS_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjc5NDg3NjA0NjcsImlhdCI6MTY0MTU2MDQ2NywiVXNlcklEIjoiMjlkYjJmMjQtMWU3OC00ODdkLTk3N2MtYjFjYWYyMGEzNzE4IiwiVXNlckxvZ2luSUQiOiJhZG1pbjEyMzQiLCJVc2VyUm9sZSI6ImFkbWluIn0.-dUyOkJBtldz3ONRFhV766ma-i-v2IvSW27Z5vQZu-E"
USER_ACCESS_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjc5NDg3NjA0MzQsImlhdCI6MTY0MTU2MDQzNCwiVXNlcklEIjoiMzk3ODhlY2UtYjQ1NC00ZTk0LWJkODktNDIzYzYzYWNhYmFiIiwiVXNlckxvZ2luSUQiOiJ1c2VyMTIzNCIsIlVzZXJSb2xlIjoidXNlciJ9.NlI4A8WRt-YlYPEC4v0bZucaKyiRzacqn8hpG-wOu20"

STORE_ID=""
STORE_NAME="mystore"
STORE_DESCRIPTION="mydescription"
STORE_DESCRIPTION2="mydescription2"

PRODUCT_ID=""
PRODUCT_NAME="myproduct"
PRODUCT_DESCRIPTION="mydescription"
PRODUCT_DESCRIPTION2="mydescription2"
PRODUCT_QUANTITY="10"
PRODUCT_QUANTITY_INCDEC="5"
PRODUCT_QUANTITY_RESULT="15"

# -- Test cases --
## Create store
echo "-- Create store start --"
RESPONSE=$(grpcurl -plaintext -format-error \
  -H "authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d "{\"name\": \"$STORE_NAME\", \"description\": \"$STORE_DESCRIPTION\"}" \
  localhost:9090 Store/CreateStore)
echo Response : $RESPONSE
if [ $? != 0 ] ; then
  EXIT_CODE=1
else
  STORE_ID=$(jq -r .id <<< "$RESPONSE")
fi
echo "-- Create store end --"

## Update store
echo "-- Update store start --"
RESPONSE=$(grpcurl -plaintext -format-error \
  -H "authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d "{\"id\": \"$STORE_ID\", \"name\": \"$STORE_NAME\", \"description\": \"$STORE_DESCRIPTION2\"}" \
  localhost:9090 Store/UpdateStore)
echo Response : $RESPONSE
if [ $? != 0 ] ; then
  EXIT_CODE=1
fi
echo "-- Update store end --"

## Get store
echo "-- Get store start --"
RESPONSE=$(grpcurl -plaintext -format-error \
  -H "authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d "{\"id\": \"$STORE_ID\"}" \
  localhost:9090 Store/GetStore)
echo Response : $RESPONSE
if [ $? != 0 ] ; then
  EXIT_CODE=1
else
  STORE_RESPONSE_DESCRIPTION=$(jq -r .description <<< "$RESPONSE")
  if [ $STORE_RESPONSE_DESCRIPTION != $STORE_DESCRIPTION2 ]; then
    echo "!! Store description info is diff !!"
    EXIT_CODE=1
  fi
fi
echo "-- Get store end --"

## Create product
echo "-- Create product start --"
RESPONSE=$(grpcurl -plaintext -format-error \
  -H "authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d "{\"name\": \"$PRODUCT_NAME\", \"storeId\": \"$STORE_ID\", \"description\": \"$PRODUCT_DESCRIPTION\", \"quantity\": $PRODUCT_QUANTITY}" \
  localhost:9090 Product/CreateProduct)
echo Response : $RESPONSE
if [ $? != 0 ] ; then
  EXIT_CODE=1
else
  PRODUCT_ID=$(jq -r .id <<< "$RESPONSE")
fi
echo "-- Create product end --"

## Update product
echo "-- Update product start --"
RESPONSE=$(grpcurl -plaintext -format-error \
  -H "authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d "{\"id\": \"$PRODUCT_ID\", \"storeId\": \"$STORE_ID\", \"name\": \"$PRODUCT_NAME\", \"description\": \"$PRODUCT_DESCRIPTION2\" ,\"quantity\": $PRODUCT_QUANTITY}" \
  localhost:9090 Product/UpdateProduct)
echo Response : $RESPONSE
if [ $? != 0 ] ; then
  EXIT_CODE=1
fi
echo "-- Update product end --"

## Get product
echo "-- Get proudct start --"
RESPONSE=$(grpcurl -plaintext -format-error \
  -H "authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d "{\"id\": \"$PRODUCT_ID\", \"storeId\": \"$STORE_ID\"}" \
  localhost:9090 Product/GetProduct)
echo Response : $RESPONSE
if [ $? != 0 ] ; then
  EXIT_CODE=1
else
  PRODUCT_RESPONSE_DESCRIPTION=$(jq -r .description <<< "$RESPONSE")
  if [ $PRODUCT_RESPONSE_DESCRIPTION != $PRODUCT_DESCRIPTION2 ]; then
    echo "!! Product description info is diff !!"
    EXIT_CODE=1
  fi
fi
echo "-- Get product end --"

## Increase product
echo "-- Increase proudct start --"
RESPONSE=$(grpcurl -plaintext -format-error \
  -H "authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d "{\"id\": \"$PRODUCT_ID\", \"storeId\": \"$STORE_ID\", \"quantity\": $PRODUCT_QUANTITY_INCDEC}" \
  localhost:9090 Product/IncreaseQuantityProduct)
echo Response : $RESPONSE
if [ $? != 0 ] ; then
  EXIT_CODE=1
else
  PRODUCT_RESPONSE_QUANTITY=$(jq -r .quantity <<< "$RESPONSE")
  if [ $PRODUCT_RESPONSE_QUANTITY != $PRODUCT_QUANTITY_RESULT ]; then
    echo "!! Product quantity is diff !!"
    EXIT_CODE=1
  fi
fi
echo "-- Increase product end --"

## Decrease product
echo "-- Decrease proudct start --"
RESPONSE=$(grpcurl -plaintext -format-error \
  -H "authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d "{\"id\": \"$PRODUCT_ID\", \"storeId\": \"$STORE_ID\", \"quantity\": $PRODUCT_QUANTITY_INCDEC}" \
  localhost:9090 Product/DecreaseQuantityProduct)
echo Response : $RESPONSE
if [ $? != 0 ] ; then
  EXIT_CODE=1
else
  PRODUCT_RESPONSE_QUANTITY=$(jq -r .quantity <<< "$RESPONSE")
  if [ $PRODUCT_RESPONSE_QUANTITY != $PRODUCT_QUANTITY ]; then
    echo "!! Product quantity is diff !!"
    EXIT_CODE=1
  fi
fi
echo "-- Decrease product end --"

## Delete product
echo "-- Delete product start --"
RESPONSE=$(grpcurl -plaintext -format-error \
  -H "authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d "{\"id\": \"$PRODUCT_ID\", \"storeId\": \"$STORE_ID\"}" \
  localhost:9090 Product/DeleteProduct)
echo Response : $RESPONSE
if [ $? != 0 ] ; then
  EXIT_CODE=1
fi
echo "-- Delete product end --"

## Delete store
echo "-- Delete store start --"
RESPONSE=$(grpcurl -plaintext -format-error \
  -H "authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d "{\"id\": \"$STORE_ID\"}" \
  localhost:9090 Store/DeleteStore)
echo Response : $RESPONSE
if [ $? != 0 ] ; then
  EXIT_CODE=1
fi
echo "-- Delete store end --"

# -- Exit --
exit $EXIT_CODE
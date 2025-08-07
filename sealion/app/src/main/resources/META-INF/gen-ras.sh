#!/usr/bin/env bash
set -e  # Exit immediately on error

# Define the key directory
KEY_DIR="app/src/main/resources/META-INF/jwt"

echo "=== Generating JWT RSA key pair ==="

# Create the directory if it doesn't exist
mkdir -p "$KEY_DIR"

# Generate RSA Private Key (PKCS#1 format)
echo "[1/5] Generating rsaPrivateKey.pem..."
openssl genrsa -out "$KEY_DIR/rsaPrivateKey.pem" 2048

# Extract Public Key
echo "[2/5] Generating publicKey.pem..."
openssl rsa -pubout -in "$KEY_DIR/rsaPrivateKey.pem" -out "$KEY_DIR/publicKey.pem"

# Convert Private Key to PKCS#8 format
echo "[3/5] Converting rsaPrivateKey.pem to privateKey.pem (PKCS#8)..."
openssl pkcs8 -topk8 -nocrypt -inform PEM -in "$KEY_DIR/rsaPrivateKey.pem" -outform PEM -out "$KEY_DIR/privateKey.pem"

# Set permissions
echo "[4/5] Setting secure permissions..."
chmod 600 "$KEY_DIR/rsaPrivateKey.pem"
chmod 600 "$KEY_DIR/privateKey.pem"

# Validate the keys
echo "[5/5] Validating keys..."
openssl rsa -in "$KEY_DIR/rsaPrivateKey.pem" -check -noout
openssl rsa -in "$KEY_DIR/privateKey.pem" -check -noout
openssl rsa -pubin -in "$KEY_DIR/publicKey.pem" -noout -text > /dev/null

echo "=== JWT RSA keys generated and validated successfully ==="
echo "Keys are located in: $KEY_DIR"

# Environment Variables Setup

This project uses environment variables to store sensitive configuration. Follow these steps to set up your environment:

## Quick Start

1. **Copy the example file:**
   ```bash
   cp .env.example .env
   ```

2. **Fill in your actual values** in the `.env` file (see below for where to get them)

3. **For frontend**, copy the example file:
   ```bash
   cd frontend
   cp .env.example .env
   ```

## Required Environment Variables

### Backend (.env in root)

- **GOOGLE_CLIENT_ID**: Get from [Google Cloud Console](https://console.cloud.google.com/)
- **GOOGLE_CLIENT_SECRET**: Get from [Google Cloud Console](https://console.cloud.google.com/)
- **JWT_SECRET**: Generate a strong random secret key (use a password generator)
- **OPENAI_API_KEY**: Get from [OpenAI Platform](https://platform.openai.com/api-keys)
- **ADMIN_USERNAME**: Admin username (default: ADMIN)
- **ADMIN_PASSWORD**: Admin password (choose a strong password)
- **MONGODB_URI**: MongoDB connection string
- **MONGODB_DATABASE**: MongoDB database name
- **SERVER_PORT**: Backend server port (default: 8080)
- **FRONTEND_URL**: Frontend URL (default: http://localhost:3000)

### Frontend (.env in frontend/)

- **VITE_GOOGLE_CLIENT_ID**: Same as backend GOOGLE_CLIENT_ID
- **VITE_API_BASE_URL**: Backend API URL (default: http://localhost:8080)

## Security Notes

⚠️ **IMPORTANT**: 
- Never commit `.env` files to version control
- The `.env` file is already in `.gitignore`
- Use `.env.example` as a template for other developers
- Change all default passwords and secrets in production
- Use strong, randomly generated secrets for JWT_SECRET

## Production Deployment

For production:
1. Use a secrets management service (AWS Secrets Manager, Azure Key Vault, etc.)
2. Set environment variables in your hosting platform
3. Never hardcode secrets in your code
4. Rotate secrets regularly


# Minigram | Backend

## Authentication

### Register

endpoint `POST` `/api/register`

```json
{
  
}
```
### Login

endpoint `POST` `/api/login`

```json
{
  
}
```
### Logout
endpoint `POST` `/api/logout`

```json
{
  
}
```
### Reset password

endpoint `POST` `/api/reset-password`

```json
{
  
}
```
## Accessing API Resource endpoints

### Posts
- Get all the posts

endpoint: `GET` `/api/posts`

```json
{
  
}
```
- Get single post

endpoint: `POST` `/api/posts/:id`

```json
{
  
}
```
- Update post 

endpoint: `PUT` `/api/posts/:id`
  
```json
{
  
}
```
- Delete post

endpoint: `DELETE` `/api/posts/:id`

```json
{
  
}
```
### Comments
- Get all the comments for a post

endpoint: `GET` `/api/comments/posts/:id`

```json
{
  
}
```
- Get single comment

endpoint: `POST` `/api/comments/:id`

```json
{
  
}
```
- Update comment

endpoint: `PUT` `/api/comments/:id`

```json
{
  
}
```
- Delete post

endpoint: `DELETE` `/api/comments/:id`

```json
{
  
}
```
### Accounts

- Get all the accounts

endpoint: `/api/accounts`

```json
{
  
}
```
- Get single acount

endpoint: `POST` `/api/accounts/:id`

```json
{
  
}
```
- Update account

endpoint: `/api/accounts/:id`

```json
{
  
}
```
- Delete account

endpoint: `DELETE` `/api/accounts/:id`

```json
{
  
}
```
### Followings

- Get followers for specific account

endpoint: `/accounts/:id/followers`

```json
{
  
}
```
- Get followings for specific account

endpoint: `POST` `/accounts/:id/following`

```json
{
  
}
```
- Follow account

endpoint: `POST` `/accounts/follow/:id`

```json
{
  
}
```
- Unfollow account

endpoint: `DELETE` `/accounts/unfollow/:id`

```json
{
  
}
```

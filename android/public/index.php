<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;
// use Slim\Http\UploadedFile;

require '../vendor/autoload.php';

require '../includes/Functions.php';
// require '../includes/braintreepayments/vendor/autoload.php';
require '../vendor/braintree/braintree_php/lib/autoload.php';


$app = new \Slim\App([
    'settings'=>[
        'displayErrorDetails'=>true
    ]
]);

$gateway = new Braintree\Gateway([
      'environment' => 'sandbox',
      'merchantId' => 'nskdd9vk7ks6bhhf',
      'publicKey' => 'n86cvzm4fcsfpbrd',
      'privateKey' => '76527d518ab7c550d0f9915949fd5816'
]);

$container = $app->getContainer();
$container['upload_products'] = "../../images/products/";
$container['upload_profile'] = "../../img/profile_pictures/";
$container['gateway'] = $gateway;
// if(file_exists(__DIR__ . "/../.env")) {
//     $dotenv = new Dotenv\Dotenv(__DIR__ . "/../");
//     $dotenv->load();
// }

// Braintree_Configuration::environment('sandbox');
// Braintree_Configuration::merchantId('nskdd9vk7ks6bhhf');
// Braintree_Configuration::publicKey('n86cvzm4fcsfpbrd');

// Braintree_Configuration::privateKey('76527d518ab7c550d0f9915949fd5816');

//$app->add(new Tuupola\Middleware\HttpBasicAuthentication([
//    "secure"=>false,
//    "users" => [
//        "belalkhan" => "123456",
//    ]
//]));

//POST METHODS
//user login
$app->post('/userlogin', function(Request $request, Response $response){

    if(!haveEmptyParameters(array('email', 'password'), $request, $response)){
        $request_data = $request->getParsedBody(); 

        $email = $request_data['email'];
        $password = $request_data['password'];

        $operation = new Functions();
        
        $query = "SELECT * FROM `users` WHERE email = '$email'";
        $count = $operation->countAll($query);
        if($count>0){
            //get the user
            $user = $operation->retrieveSingle($query);
            $hashed_password = $user['password'];

            if(password_verify($password, $hashed_password)){
                
                if($user['account_status'] == 1){
                    
                    //get user but filter out some values
                    $user = $operation->retrieveSingle("SELECT *FROM `users` WHERE email = '$email'");
                    $user_id = $user['user_id'];

                    $response_data = array();
                    
                    $response_data['error']=false; 
                    $response_data['message'] = 'Login Successful';
                    $response_data['user'] = $user;

                    //retrieve all other data
                    $getUsers = $operation->retrieveMany("SELECT *FROM users");
                    $getSuppliers = $operation->retrieveMany("SELECT *FROM supplier");
                    $getProducts = $operation->retrieveMany("SELECT *FROM products");
                    $getCategories = $operation->retrieveMany("SELECT *FROM categories");

                    $response_data['users'] = $getUsers;
                    $response_data['suppliers'] = $getSuppliers;
                    $response_data['products'] = $getProducts;
                    $response_data['categories'] = $getCategories;

                    $response->write(json_encode($response_data));

                    return $response
                        ->withHeader('Content-type', 'application/json')
                        ->withStatus(200); 
                }else{
                    $response_data = array();

                    $response_data['error']=true; 
                    $response_data['message'] = 'Account Suspended';

                    $response->write(json_encode($response_data));

                    return $response
                        ->withHeader('Content-type', 'application/json')
                        ->withStatus(201); 
                }
            }else{
                $response_data = array();

                $response_data['error']=true; 
                $response_data['message'] = 'Invalid credential';

                $response->write(json_encode($response_data));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(201);  
            }
            
        }else{
           $response_data = array();

            $response_data['error']=true; 
            $response_data['message'] = 'User does not exist';

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(201);  
        }        
    }

    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(422);    
});

//add user
$app->post('/add_user', function(Request $request, Response $response){

    if(!haveEmptyParameters(array('fullname','phone','email', 'password','user_role'), $request, $response)){
        $request_data = $request->getParsedBody(); 

        $fullname = $request_data['fullname'];
        $phone = $request_data['phone'];
        $user_role = $request_data['user_role'];
        $email = $request_data['email'];
        $pass = $request_data['password'];
        //encyrpt password
        $password=password_hash($pass, PASSWORD_DEFAULT);

        $operation = new Functions();
        
        $query = "SELECT * FROM `users` WHERE email = '$email' ";
        $count = $operation->countAll($query);
        if($count==0){

            $table = "users";
            $data = [
                'fullname'=>"$fullname",
                'phone'=>"$phone",
                'email'=>"$email",
                'password'=>"$password",
                'user_role'=>"$user_role"
            ];

            if ($operation->insertData($table,$data) == 1) {
                # code...
                 $response_data = array();
            
                $response_data['error']=false; 
                $response_data['message'] = 'Account created, please login to continue!';
                   //retrieve all other data
                    $getUsers = $operation->retrieveMany("SELECT *FROM users");
                    $getSuppliers = $operation->retrieveMany("SELECT *FROM supplier");
                    $getProducts = $operation->retrieveMany("SELECT *FROM products");
                    $getCategories = $operation->retrieveMany("SELECT *FROM categories");

                    $response_data['users'] = $getUsers;
                    $response_data['suppliers'] = $getSuppliers;
                    $response_data['products'] = $getProducts;
                    $response_data['categories'] = $getCategories;


                $response->write(json_encode($response_data));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(200); 
            }else{
                 $response_data = array();

                $response_data['error']=true; 
                $response_data['message'] = 'An error occured while registering, try again later!';

                $response->write(json_encode($response_data));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(201); 
            }

           
      
            
        }else{
           $response_data = array();

            $response_data['error']=true; 
            $response_data['message'] = 'Email is taken, try another email';

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(201);  
        }        
    }

    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(422);    
});


//add supplier
$app->post('/add_supplier', function(Request $request, Response $response){

    if(!haveEmptyParameters(array('fullname','phone','email','address','notes','is_default'), $request, $response)){
        $request_data = $request->getParsedBody(); 

        $fullname = $request_data['fullname'];
        $phone = $request_data['phone'];
        $user_role = 'supplier';
        $email = $request_data['email'];
        $notes = $request_data['notes'];
        $address = $request_data['address'];
        $is_default = $request_data['is_default'];

        $operation = new Functions();
        
        $query = "SELECT * FROM `users` WHERE email = '$email'";

        $count = $operation->countAll($query);
        if($count==0){

            $table = "users";
            $data = [
                'fullname'=>"$fullname",
                'phone'=>"$phone",
                'email'=>"$email",
                'user_role'=>"$user_role"
            ];

            if ($operation->insertData($table,$data) == 1) {
                $table ="supplier";
                 if ($is_default == true) {
                    $where = "is_default = '$is_default'";
                    $false = "0";
                    $data = [
                        'is_default'=>"$false"
                    ];
                    $operation->updateData($table,$data, $where);
                }

                $getUser = $operation->retrieveSingle($query);
                $user_id = $getUser['user_id'];

                //get user id
                $table = "supplier";
                $data = [
                    'user_id'=>"$user_id",
                    'address'=>"$address",
                    'notes' =>"$notes",
                    'is_default'=>"$is_default"
                ];



                if ($operation->insertData($table,$data) == 1) {
                    // code...

                    $response_data = array();
            
                    $response_data['error']=false; 
                    $response_data['message'] = 'Supplier has been added';

                       //retrieve all other data
                    $getUsers = $operation->retrieveMany("SELECT *FROM users");
                    $getSuppliers = $operation->retrieveMany("SELECT *FROM supplier");
                    $getProducts = $operation->retrieveMany("SELECT *FROM products");
                    $getCategories = $operation->retrieveMany("SELECT *FROM categories");

                    $response_data['users'] = $getUsers;
                    $response_data['suppliers'] = $getSuppliers;
                    $response_data['products'] = $getProducts;
                    $response_data['categories'] = $getCategories;

                    $response->write(json_encode($response_data));

                    return $response
                        ->withHeader('Content-type', 'application/json')
                        ->withStatus(200); 
                }else{

                    $response_data = array();

                    $response_data['error']=true; 
                    $response_data['message'] = 'An error occured while finalizing adding supplier, try again later!';

                    $response->write(json_encode($response_data));

                    return $response
                        ->withHeader('Content-type', 'application/json')
                        ->withStatus(201); 

                }

            }else{
                 $response_data = array();

                $response_data['error']=true; 
                $response_data['message'] = 'An error occured while adding supplier, try again later!';

                $response->write(json_encode($response_data));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(201); 
            }

           
      
            
        }else{
           $response_data = array();

            $response_data['error']=true; 
            $response_data['message'] = 'Email is taken, try another email';

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(201);  
        }        
    }

    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(422);    
});


//add category
$app->post('/add_category', function(Request $request, Response $response){

    if(!haveEmptyParameters(array('category_name','notes'), $request, $response)){
        $request_data = $request->getParsedBody(); 

        $category_name = $request_data['category_name'];
        $notes = $request_data['notes'];

        $operation = new Functions();
        
        $query = "SELECT * FROM `categories` WHERE category_name = '$category_name'";

        $count = $operation->countAll($query);
        if($count==0){

            $table = "categories";
            $data = [
                'category_name'=>"$category_name",
                'category_note'=>"$notes",
            ];

            if ($operation->insertData($table,$data) == 1) {
      
                $response_data = array();
        
                $response_data['error']=false; 
                $response_data['message'] = 'Category has been added';

                   //retrieve all other data
                    $getUsers = $operation->retrieveMany("SELECT *FROM users");
                    $getSuppliers = $operation->retrieveMany("SELECT *FROM supplier");
                    $getProducts = $operation->retrieveMany("SELECT *FROM products");
                    $getCategories = $operation->retrieveMany("SELECT *FROM categories");

                    $response_data['users'] = $getUsers;
                    $response_data['suppliers'] = $getSuppliers;
                    $response_data['products'] = $getProducts;
                    $response_data['categories'] = $getCategories;


                $response->write(json_encode($response_data));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(200); 
            }else{
                 $response_data = array();

                $response_data['error']=true; 
                $response_data['message'] = 'An error occured while adding category, try again later!';

                $response->write(json_encode($response_data));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(201); 
            }
            
        }else{
           $response_data = array();

            $response_data['error']=true; 
            $response_data['message'] = 'Category name is already in use, try a different name!';

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(201);  
        }        
    }

    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(422);    
});


//add product
$app->post('/add_product', function(Request $request, Response $response) {

    if(!haveEmptyParameters(array('supplier_id','category_id','product_name','product_code','product_cost','product_price','product_quantity','product_threshold','product_description'), $request, $response)){
        $request_data = $request->getParsedBody();

        $directory = $this->get('upload_products');

        $uploadedFiles = $request->getUploadedFiles();
        $operation = new Functions();

        $supplier_id = $request_data['supplier_id'];
        $category_id = $request_data['category_id'];
        $product_code = $request_data['product_code'];
        $product_name = $request_data['product_name'];
        $product_cost = $request_data['product_cost'];
        $product_price = $request_data['product_price'];
        $product_quantity = $request_data['product_quantity'];
        $product_threshold = $request_data['product_threshold'];
        $product_description = $request_data['product_description'];

        // handle single input with single file upload
        $uploadedFile = $uploadedFiles['file'];
        if ($uploadedFile->getError() === UPLOAD_ERR_OK) {
            $filename = moveUploadedFile($directory, $uploadedFile);

             $table = "products";
             $data = [
                'supplier_id'=>"$supplier_id",
                'category_id'=>"$category_id",
                'product_name'=>"$product_name",
                'product_code'=>"$product_code",
                'product_cost'=>"$product_cost",
                'product_price'=>"$product_price",
                'product_quantity'=>"$product_quantity",
                'product_threshold'=>"$product_threshold",
                'product_img_url'=>"$filename",
                'product_description'=>"$product_description"
             ];
             if ($operation->insertData($table,$data) == 1) {
                 // code...
                $response_data = array();

                $response_data['error']=false; 
                $response_data['message'] = 'Product has been added!';

                   //retrieve all other data
                    $getUsers = $operation->retrieveMany("SELECT *FROM users");
                    $getSuppliers = $operation->retrieveMany("SELECT *FROM supplier");
                    $getProducts = $operation->retrieveMany("SELECT *FROM products");
                    $getCategories = $operation->retrieveMany("SELECT *FROM categories");

                    $response_data['users'] = $getUsers;
                    $response_data['suppliers'] = $getSuppliers;
                    $response_data['products'] = $getProducts;
                    $response_data['categories'] = $getCategories;

                $response->write(json_encode($response_data));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(200);
             }else{
                $response_data = array();

                $response_data['error']=true; 
                $response_data['message'] = 'An error occured while adding product, try again later!';

                $response->write(json_encode($response_data));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(201);
             }


        }else{

            $response_data = array();

            $response_data['error']=true; 
            $response_data['message'] = 'An error occured while adding product, try again later!';

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(201); 
        }



        // handle multiple inputs with the same key
        // foreach ($uploadedFiles['example2'] as $uploadedFile) {
        //     if ($uploadedFile->getError() === UPLOAD_ERR_OK) {
        //         $filename = moveUploadedFile($directory, $uploadedFile);
        //         $response->write('uploaded ' . $filename . '<br/>');
        //     }
        // }

       

      
    }


    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(422); 
});










//GET METHODS

//get all data || gets data only when there is a user in database
$app->get('/all_data',function(Request $request, Response $response){
    

    $operation = new Functions();
    $query = "SELECT *FROM users ";
    
    if($operation->countAll($query) > 0 ){
        $my_orders = $operation->retrieveMany($query);
        $response_data = array();

        $response_data['error']=false; 
        $response_data['message'] = 'All data';
        
        //retrieve all other data
        $getUsers = $operation->retrieveMany("SELECT *FROM users");
        $getSuppliers = $operation->retrieveMany("SELECT *FROM supplier");
        $getProducts = $operation->retrieveMany("SELECT *FROM products");
        $getCategories = $operation->retrieveMany("SELECT *FROM categories");

        $response_data['users'] = $getUsers;
        $response_data['suppliers'] = $getSuppliers;
        $response_data['products'] = $getProducts;
        $response_data['categories'] = $getCategories;


        $response->write(json_encode($response_data));

        return $response
            ->withHeader('Content-type', 'application/json')
            ->withStatus(200);
        
    }else{
        $response_data = array();
                    
        $response_data['error']=true; 
        $response_data['message'] = 'Nothing on server!';

        $response->write(json_encode($response_data));

        return $response
            ->withHeader('Content-type', 'application/json')
            ->withStatus(201);
    }
     return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(422); 
    
});




function haveEmptyParameters($required_params, $request, $response){
    $error = false; 
    $error_params = '';
    $request_params = $request->getParsedBody(); 

    foreach($required_params as $param){
        if(!isset($request_params[$param]) || strlen($request_params[$param])<=0){
            $error = true; 
            $error_params .= $param . ', ';
        }
    }

    if($error){
        $error_detail = array();
        $error_detail['error'] = true; 
        $error_detail['message'] = 'Required parameters ' . substr($error_params, 0, -2) . ' are missing or empty';
        $response->write(json_encode($error_detail));
    }
    return $error; 
}


/**
 * Moves the uploaded file to the upload directory and assigns it a unique name
 * to avoid overwriting an existing uploaded file.
 *
 * @param string $directory directory to which the file is moved
 * @param UploadedFile $uploadedFile file uploaded file to move
 * @return string filename of moved file
 */
function moveUploadedFile($directory, Slim\Http\UploadedFile $uploadedFile){
    $extension = pathinfo($uploadedFile->getClientFilename(), PATHINFO_EXTENSION);
    $basename = bin2hex(random_bytes(8)); // see http://php.net/manual/en/function.random-bytes.php
    $filename = sprintf('%s.%0.8s', $basename, $extension);

    $uploadedFile->moveTo($directory . DIRECTORY_SEPARATOR . $filename);

    return $filename;
}


$app->run();

